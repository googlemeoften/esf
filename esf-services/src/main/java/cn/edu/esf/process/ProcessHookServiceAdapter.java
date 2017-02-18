package cn.edu.esf.process;/**
 * Created by HeYong on 2017/2/13.
 */

import cn.edu.esf.model.matedata.ServiceMetadata;

/**
 * Description:作为适配器的存在
 *
 * @author heyong
 * @Date 2017/2/13
 */
public abstract class ProcessHookServiceAdapter implements ProcessHookService{
    /**
     * 在设置调用的ServiceMetadata之后
     */
    public void afterConsume(ServiceMetadata metadata){};

    /**
     * 在发布ServiceMetadata之后
     */
    public void afterPublish(ServiceMetadata metadata){};

    /**
     * 在设置调用的ServiceMetadata之前
     */
    public void preConsume(ServiceMetadata metadata){};

    /**
     * 在发布ServiceMetadata之前
     */
    public void prePublish(ServiceMetadata metadata){};
}
