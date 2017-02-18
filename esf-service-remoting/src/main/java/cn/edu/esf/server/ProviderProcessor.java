package cn.edu.esf.server;/**
 * Created by HeYong on 2017/2/13.
 */

import cn.edu.esf.Connection;
import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.domain.ESFResponse;
import cn.edu.esf.moniter.MonitorService;
import cn.edu.esf.pool.ThreadPoolManager;
import cn.edu.esf.server.output.ServerOutput;
import cn.edu.esf.tps.TPSLimitService;
import cn.edu.esf.tps.TPSResult;
import cn.edu.esf.utils.ESFServiceContainer;
import cn.edu.esf.utils.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Description:ESF服务端用来处理ESF请求的处理器。该处理器实例注册给ESF'S REMOTING SERVER使用。
 *
 * @author heyong
 * @Date 2017/2/13
 */
public class ProviderProcessor implements RpcRequestProcessor {
    private static final Logger LOGGE = LoggerFactory.getLogger(ProviderProcessor.class);

    private static final String ESF_PROVIDER_TIMEOUT = "ESF-Provider-Timeout";
    private static final String ESF_PROVIDER_DETAIL = "ESF-ProviderDetail";
    private static final String ESF_PROVIDER_RTMINMAX = "ESF-ProviderRtMinmax";
    private static final String ESF_PROVIDER_DETAIL_BIZ_EXCEPTION = "ESF-ProviderDetail-BizException";
    private static final String ESF_PROVIDER_DETAIL_EXCEPTION = "ESF-ProviderDetail-Exception";
    private static final String ESF_PROVIDER_ACTIVE_THREAD = "ESF-Provider-ActiveThread";
    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderProcessor.class);


    private static final Field causeField;

    static {
        try {
            causeField = Throwable.class.getDeclaredField("cause");
            causeField.setAccessible(true);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private final TPSLimitService tpsLimitService = ESFServiceContainer.getInstance(TPSLimitService.class);
    private final List<MonitorService> monitorServices = ESFServiceContainer.getInstances(MonitorService.class);
    private final ThreadPoolManager threadPoolManager = ThreadPoolUtil.getThreadPoolManager();


    @Override
    public Executor getExecutor(String serviceName) {
        return threadPoolManager.getExecutor(serviceName);
    }

    @Override
    public ThreadPoolManager getThreadPoolManager() {
        return threadPoolManager;
    }

    @Override
    public void handleRequest(ESFRequest appRequest, ServerOutput output) {
        final String serviceUniqueName = appRequest.getServiceName();
        final String methodName = appRequest.getMethodName();
        final Connection connection = output.getConnection();
        String clientIP = ((InetSocketAddress) connection.getRemotingAddress()).getAddress().getHostAddress();

        //限流
        try {
            TPSResult result = tpsLimitService.process(appRequest);
            if (!result.isAllowed()) {
                output.writeHSFResponse(processTpsNotAllowed(appRequest, serviceUniqueName, methodName, clientIP, result));
                return;
            }
        } catch (Throwable e) {
            // TPS出错，继续执行
            LOGGER.warn(MessageFormat.format(
                    "[ESF-Provider] TPS rule executed error, TPS rule: {0}, ESF request: {1}.", new Object[]{
                            tpsLimitService, appRequest}));
        }

        ESFResponse response = null;
        Object[] Args = appRequest.getMethosArgs();
        String[] ArgTypes = appRequest.getMethodArgTypes();

        response = handleRequest0(appRequest, output, Args, ArgTypes);

        output.writeHSFResponse(response);
    }

    private ESFResponse processTpsNotAllowed(final ESFRequest hsfRequest, final String serviceUniqueName,
                                             final String methodName, final String clientIp, TPSResult result) {
        ESFResponse response = new ESFResponse();
        response.setErrorMsg(MessageFormat.format("[ESF-Provider] The request for [{0}] [{1}] from [{2}] is blocked by TPS rule: {3}.", new Object[]{
                serviceUniqueName, methodName, clientIp, result.getMessage()}));
        return response;
    }

    private ESFResponse handleRequest0(final ESFRequest request, final ServerOutput output, final Object[] Args, final String[] ArgTypes) {
        final String serviceUniqueName = request.getServiceName();
        final String methodName = request.getMethodName();
        final Object[] args = request.getMethosArgs();
        final String[] argTypes = request.getMethodArgTypes();

        StringBuilder sb = new StringBuilder();
        sb.append(serviceUniqueName + "\n").append(methodName + "\n");
        for (int i = 0; i < args.length; i++) {
            sb.append(argTypes[i] + ":").append(args + "\n");
        }

        Object result = null;
        try {
            Class clazz = Class.forName(serviceUniqueName);

            Object instance = clazz.newInstance();

            Class[] argTypesClass = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypesClass[i] = Class.forName(argTypes[i]);
            }

            Method method = clazz.getMethod(methodName, argTypesClass);
            result = method.invoke(instance, args);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        ESFResponse response = new ESFResponse();
        response.setResponseObject(result);
        return response;
    }
}
