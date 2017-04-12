package cn.edu.esf.api;

import cn.edu.esf.model.matedata.ServiceMetadata;
import cn.edu.esf.process.ProcessService;
import cn.edu.esf.remoting.GenericService;
import cn.edu.esf.utils.ESFServiceContainer;
import cn.edu.esf.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/4/11
 */
public class ESFApiConsumerBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(ESFApiConsumerBean.class);
    private static final String DEFAULT_VERSION = "1.0.0";
    private static final String DEFAULT_GROUP = "ESF";
    private final ServiceMetadata metadata = new ServiceMetadata(false);
    private final AtomicBoolean inited = new AtomicBoolean(false);
    private List<String> asyncallMethods = null;

    public ESFApiConsumerBean() {
        metadata.setGroup(DEFAULT_GROUP);
        metadata.setVersion(DEFAULT_VERSION);
        metadata.setProxyStyle("JDK");
    }

    public Object getTarget() {
        return metadata.getTarget();
    }

    public void init() throws Exception {
        if (!inited.compareAndSet(false, true)) {
            return;
        }

        String interfaceName = metadata.getInterfaceName();
        Class<?> interfaceClass = null;

        try {
            interfaceClass = Class.forName(interfaceName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("ConsumerBean中指定的接口类不存在[");
            errorMsg.append(interfaceName).append("].");
            interfaceClass = GenericService.class;
        }

        metadata.setIfClazz(interfaceClass);
        // 加入到manager里边
        //ServiceMetadataManager.allServiceMetadata.put(metadata.getUniqueName(), metadata);
        metadata.initUniqueName();
        ProcessService processService = ESFServiceContainer.getInstance(ProcessService.class);

        try {
            metadata.setTarget(processService.consume(metadata));
            LOGGER.warn("成功生成对接口为[" + metadata.getInterfaceName() + "]版本为[" + metadata.getVersion() + "]的HSF服务调用的代理！");
        } catch (Exception e) {
            LOGGER.error("生成对接口为[" + metadata.getInterfaceName() + "]版本为[" + metadata.getVersion() + "]的HSF服务调用的代理失败", e);
            // since 2007，一旦初始化异常就抛出
            throw e;
        }
    }

    public void setGroup(String group) {
        metadata.setGroup(group);
    }

    public void setVersion(String version) {
        metadata.setVersion(version);
    }

    public void setInterfaceName(String interfaceName) {
        metadata.setInterfaceName(interfaceName);
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        metadata.setIfClazz(interfaceClass);
    }

    public void setMaxThreadPool(String poolSize) {
        metadata.setCorePoolSize(poolSize);
    }

    public void setTarget(String target) {
        if (StringUtils.isBlank(target)) {
            metadata.addProperty("target", target);
        }
    }

    public void setSecureKey(String secureKey) {
//        metadataService
    }

    /**
     * 用于可靠异步调用
     *
     * @param callbackHandler
     */
    public void setCallbackHandler(Object callbackHandler) {
        metadata.setCallbackInvoker(callbackHandler);
    }

    public void setInvokeContextThreadLocal(ThreadLocal<Serializable> invokeContext) {
        metadata.setInvokeContext(invokeContext);
    }

    public ServiceMetadata getMetadata() {
        return metadata;
    }

    public AtomicBoolean getInited() {
        return inited;
    }
}
