package cn.edu.esf.api;

import cn.edu.esf.constant.TRConstants;
import cn.edu.esf.exception.ESFException;
import cn.edu.esf.model.matedata.ServiceMetadata;
import cn.edu.esf.process.ProcessService;
import cn.edu.esf.utils.ESFServiceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/4/11
 */
public class ESFApiProviderBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(ESFApiProviderBean.class);
    private final ServiceMetadata metadata = new ServiceMetadata(true);
    private final AtomicBoolean inited = new AtomicBoolean(false);
    private final AtomicBoolean isPublish = new AtomicBoolean(false);

    public ESFApiProviderBean() {
        metadata.setGroup("ESF");
        metadata.setVersion("1.0.0");
    }

    public void init() throws Exception {
        if (!inited.compareAndSet(false, true)) {
            return;
        }
        checkConfig();
        this.publish();
    }

    public void publish() {
        // 防止一个服务被发布多次
        if (!isPublish.compareAndSet(false, true)) {
            return;
        }

        try {
            ESFServiceContainer.getInstance(ProcessService.class).publish(metadata);
        } catch (ESFException e) {
            e.printStackTrace();
            LOGGER.error("接口[" + metadata.getInterfaceName() + "]版本[" + metadata.getVersion() + "]发布为HSF服务失败", e);
        }
    }


    public void checkConfig() {
        metadata.initUniqueName();
        StringBuilder errorMsg = new StringBuilder();
        Object target = metadata.getTarget();
        if (target == null) {
            errorMsg.append("未配置需要发布为服务的Object，服务名为:").append(metadata.getUniqueName());
        }

        String serviceInterface = metadata.getInterfaceName();

        Class<?> interfaceClass = null;
        try {
            interfaceClass = Class.forName(serviceInterface);
            metadata.setIfClazz(interfaceClass);
            if (!interfaceClass.isInterface()) {
                errorMsg.append("ProviderBean中指定的服务类型不是接口[");
                errorMsg.append(serviceInterface).append("].");
            }

            if (target != null && !interfaceClass.isAssignableFrom(target.getClass())) {
                errorMsg.append("真实的服务对象[").append(target);
                errorMsg.append("]没有实现指定接口[").append(serviceInterface).append("].");
            }


        } catch (ClassNotFoundException e) {
           // e.printStackTrace();
            errorMsg.append("ProviderBean中指定的接口类不存在[");
            errorMsg.append(serviceInterface).append("].");
        }

    }

    public ESFApiProviderBean setClientTimeout(int clientTimeout) {
        metadata.addProperty(TRConstants.CONNECT_TIMEOUT_KEY, "" + clientTimeout);
        return this;
    }

    public ESFApiProviderBean setCorePoolSize(String corePoolSize) {
        metadata.setCorePoolSize(corePoolSize);
        return this;
    }

    public ESFApiProviderBean setMaxPoolSize(String maxPoolSize) {
        metadata.setMaxPoolSize(maxPoolSize);
        return this;
    }

    public ESFApiProviderBean setSerializeType(String serializeType) {
        metadata.addProperty(TRConstants.PREFER_SERIALIZIER, serializeType);
        return this;
    }

    public ESFApiProviderBean setServiceGroup(String serviceGroup) {
        metadata.setGroup(serviceGroup);
        return this;
    }

    public ESFApiProviderBean setServiceVersion(String version) {
        metadata.setVersion(version);
        return this;
    }

    public ESFApiProviderBean setServiceInterface(String serviceInterface) {
        metadata.setInterfaceName(serviceInterface);
        return this;
    }

    public ESFApiProviderBean setTarget(Object target) {
        metadata.setTarget(target);
        return this;
    }

    public ServiceMetadata getMetadata() {
        return metadata;
    }

    public AtomicBoolean getInited() {
        return inited;
    }

    public AtomicBoolean getIsPublish() {
        return isPublish;
    }

    public void setServiceInterfaceClass(Class<?> interfaceClass){
        metadata.setIfClazz(interfaceClass);
    }
}
