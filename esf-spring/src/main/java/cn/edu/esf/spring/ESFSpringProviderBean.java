package cn.edu.esf.spring;

import cn.edu.esf.api.ESFApiProviderBean;
import cn.edu.esf.model.matedata.ServiceMetadata;
import cn.edu.esf.utils.AppInfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Created by HeYong on 2017/4/11.
 */
public class ESFSpringProviderBean implements InitializingBean, ApplicationContextAware, ApplicationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ESFSpringProviderBean.class);

    private final ESFApiProviderBean provider = new ESFApiProviderBean();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        provider.init();
    }

    public void init() throws Exception {
        if (!provider.getInited().compareAndSet(false, true)) {
            return;
        }

        provider.checkConfig();
        provider.publish();
    }

    public void setClientTimeout(int clientTimeout) {
        provider.setClientTimeout(clientTimeout);
    }

    public void setCorePoolSize(String corePoolSize) {
        provider.setCorePoolSize(corePoolSize);
    }

    public void setMaxPoolSize(String maxPoolSize) {
        provider.setMaxPoolSize(maxPoolSize);
    }

    public void setSerializeType(String serializeType) {
        provider.setSerializeType(serializeType);
    }

    public void setServiceGroup(String serviceGroup) {
        provider.setServiceGroup(serviceGroup);
    }

    public void setServiceInterface(String serviceInterface) {
        provider.setServiceInterface(serviceInterface);
    }

    public void setServiceInterfaceClass(Class<?> serviceInterfaceClass) {
        provider.setServiceInterfaceClass(serviceInterfaceClass);
    }

    public void setServiceName(String serviceName) {
//        provider.serviceName(serviceName);
    }

    public void setServiceVersion(String serviceVersion) {
        provider.setServiceVersion(serviceVersion);
    }

    public void setTarget(Object target) {
        provider.setTarget(target);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            ESFSpringProviderBean.this.provider.publish();
            setAppInitedStatus();
        } else if (event instanceof ContextClosedEvent) {
//            if (AppInfoUtils.appInited.compareAndSet(true, false)) {
//                LOGGER.info("Spring容器关闭，设置应用初始化状态为未初始化！");
//            }
        }
    }

    private void setAppInitedStatus() {
        // 最后一个HsfspringBean发布完的时候才设置状态(providerBean都初始化完成了，但是可能会publish失败)
        if (0 == (--AppInfoUtils.hsfSpringBeanCountDown)) {
//            if (AppInfoUtils.appInited.compareAndSet(false, true)) {
//                LOGGER.info("所有hsfSpringBean初始化完成，Spring容器初始化完成，可以通过pandora查询应用启动状态。");
//            }
        }
    }

    public ServiceMetadata getMetaData(){
        return provider.getMetadata();
    }
}

