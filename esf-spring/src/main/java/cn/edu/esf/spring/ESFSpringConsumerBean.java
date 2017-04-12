package cn.edu.esf.spring;

import cn.edu.esf.api.ESFApiConsumerBean;
import cn.edu.esf.model.matedata.ServiceMetadata;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/4/11
 */
public class ESFSpringConsumerBean implements FactoryBean, InitializingBean {
    private final ESFApiConsumerBean consumerBean = new ESFApiConsumerBean();
    private final ServiceMetadata metadata = consumerBean.getMetadata();

    // 在同步初始化的时候，最多等待多久地址推送到达
    private long maxWaitAddressTimeMS = 3000;

    @Override
    public Object getObject() throws Exception {
        return consumerBean.getTarget();
    }

    @Override
    public Class<?> getObjectType() {
        if (metadata.getInterfaceName() == null) {
            return null;
        }

        if (metadata.getIfClazz() == null) {
            return null;
        } else {
            return metadata.getIfClazz();
        }
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        consumerBean.init();
    }

    /**
     * 设置服务所属的组
     */
    public void setGroup(String group) {
        consumerBean.setGroup(group);
    }

    /**
     * 设置调用的服务的版本
     */
    public void setVersion(String version) {
        consumerBean.setVersion(version);
    }

    /**
     * 设置接口名。如果该接口无法装载到，则抛出{@link IllegalArgumentException}
     */
    public void setInterfaceName(String interfaceName) {
        consumerBean.setInterfaceName(interfaceName);
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        consumerBean.setInterfaceClass(interfaceClass);
    }

    /**
     * 用户调用的线程通常希望控制调用的线程池大小，该属性用来设置用户调用的线程池大小。
     *
     * @param poolSize
     */

    public void setMaxThreadPool(String poolSize) {
        consumerBean.setMaxThreadPool(poolSize);
    }

    /**
     * 设置调用的服务的目标地址
     */
    public void setTarget(String target) {
        consumerBean.setTarget(target);
    }

    public void setConnectionNum(int connectionNum) {
//        consumerBean.set(connectionNum);
    }

    public void setSecureKey(String secureKey) {
        consumerBean.setSecureKey(secureKey);
    }

    // 全局超时
    public void setClientTimeout(int clientTimeout) {
//        consumerBean.setClientTimeout(clientTimeout);
    }




}
