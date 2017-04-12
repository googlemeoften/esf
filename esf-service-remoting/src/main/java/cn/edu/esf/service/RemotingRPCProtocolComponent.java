package cn.edu.esf.service;

import cn.edu.esf.RemotingURL;
import cn.edu.esf.address.AddressService;
import cn.edu.esf.config.ConfigurationService;
import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.exception.ESFException;
import cn.edu.esf.model.matedata.ServiceMetadata;
import cn.edu.esf.remoting.InvokeService;
import cn.edu.esf.remoting.ProviderServer;
import cn.edu.esf.remoting.RPCProtocolService;
import cn.edu.esf.utils.ESFConstants;
import cn.edu.esf.utils.ESFServiceContainer;
import cn.edu.esf.utils.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/19
 */
public class RemotingRPCProtocolComponent implements RPCProtocolService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemotingRPCProtocolComponent.class);

    private final AtomicBoolean isProviderStarted = new AtomicBoolean(false);
    private static final int RETRY_TIMES = 3;
    private final AddressService addressService = ESFServiceContainer.getInstance(AddressService.class);
    private final ConfigurationService configurationService = ESFServiceContainer.getInstance(ConfigurationService.class);
    private final ProviderServer providerServer = ESFServiceContainer.getInstance(ProviderServer.class);
    private final static ConcurrentHashMap<String, InvokeService> invokeServices = new ConcurrentHashMap<>();

    static {
        for (InvokeService invokeService : ESFServiceContainer.getInstances(InvokeService.class)) {
            System.out.println(invokeService.getKey());
            invokeServices.put(invokeService.getKey(), invokeService);
        }
    }

    private ThreadLocal<String> targetURLCache = new ThreadLocal<String>();

    @Override
    public Object invoke(ServiceMetadata metadata, Method method, String methodName, Class<?>[] parameterTypes, Object[] args) throws ESFException, Throwable {
        final long beginTime = System.currentTimeMillis();
        final String serviceName = metadata.getUniqueName();

        args = (args == null) ? new Object[0] : args;

        Object result = null;


        final ESFRequest request = new ESFRequest();
        request.setServiceName(metadata.getInterfaceName());
        request.setMethodName(methodName);
        request.setMethodArgTypes(createParamSignature(parameterTypes));
        request.setMethosArgs(args);

        //设置消费端名称,此处写死
        String appName = "appName";
        if (appName == null) {
            appName = "appName";
        }

        // request.setRequestProps(ESFConstants.CONSUMER_APP_NAME, appName);

        //String secureKey = metadata.getSecureKey();

        String targetURL = NetUtil.getLocalAddress();


        Object appResponse = null;

        System.out.println("request" + request);
        try {
            appResponse = invoke0(request, metadata, targetURL);
        } catch (ESFException e) {
            throw e;
        }

        // 检查对端返回的业务层对象: 如果返回的是异常对象，则重新抛出异常
        if (appResponse instanceof Throwable) {
            throw (Throwable) appResponse;
        }

        return appResponse;

    }

    @Override
    public void registerProvider(ServiceMetadata metadata) throws ESFException {
        // 仅启动一次HSF SERVER
        if (isProviderStarted.compareAndSet(false, true)) {
            try {
                providerServer.startHSFServer();

                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            shutdownGracefully();
                        } catch (Exception e) {
                            LOGGER.warn("Exception happens during stop server:", e);
                        }
                    }
                });
            } catch (Exception e) {
                throw new ESFException("启动ESF Server失败");
            }
        }

        // 分配线程池
        int corePoolSize = metadata.getCorePoolSize();
        int maxPoolSize = metadata.getMaxPoolSize();
        if (corePoolSize > 0 && maxPoolSize > 0 && maxPoolSize >= corePoolSize) {
            providerServer.allocThreadPool(metadata.getUniqueName(), corePoolSize, maxPoolSize);
        }

        // 注册对象到HSF Server上
        providerServer.addMetadata(metadata.getUniqueName(), metadata);
    }

    @Override
    public boolean validTarget(String targetURL) {
        return false;
    }


    public Object invoke0(ESFRequest request, ServiceMetadata metadata, String targetURL) throws ESFException {

        final String serviceName = metadata.getUniqueName();
        String invokeType = "SYNC";
        InvokeService invokeService = invokeServices.get(invokeType);

        RemotingURL targetUrl = new RemotingURL(NetUtil.getLocalAddress(), "ESF", NetUtil.getLocalAddress(),
                configurationService.getESFServerPort(), null, null);

        return invokeService.invoke(request, metadata, targetUrl, (byte) 1, 4000);
    }

    /**
     * 获取参数类型
     *
     * @param args
     * @return
     */
    private String[] createParamSignature(Class<?>[] args) {
        if (args == null || args.length == 0) {
            return new String[]{};
        }
        String[] paramSig = new String[args.length];
        for (int x = 0; x < args.length; x++) {
            paramSig[x] = args[x].getName();
        }
        return paramSig;
    }

    public void shutdownGracefully() throws Exception {
//        for (ProviderServiceModel serviceModel : ApplicationModel.instance().allProvidedServices()) {
//            serviceModel.getMetadata().fireMetadataBeforeChanged();
//            metadataService.unregister(serviceModel.getMetadata());
//            // HSFServiceContainer.getInstance(MetaSupportService.class).unregister(
//            // serviceModel.getMetadata());
//        }
//
//        Thread.sleep(ESFServiceContainer.getInstance(ConfigurationService.class)
//                .getshutdownHookWaitTime());
        providerServer.stopHSFServer();
    }
}
