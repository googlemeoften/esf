package cn.edu.esf.process;

import cn.edu.esf.model.matedata.ServiceMetadata;

/**
 * 描述：用于扩展HSF服务发布与消费总控流程，能够在服务发布和消费的前面和后面插入需要的处理逻辑。
 *
 */
public interface ProcessHookService {

    /**
     * 在设置调用的ServiceMetadata之后
     */
    public void afterConsume(ServiceMetadata metadata);

    /**
     * 在发布ServiceMetadata之后
     */
    public void afterPublish(ServiceMetadata metadata);

    /**
     * 在设置调用的ServiceMetadata之前
     */
    public void preConsume(ServiceMetadata metadata);

    /**
     * 在发布ServiceMetadata之前
     */
    public void prePublish(ServiceMetadata metadata);
}
