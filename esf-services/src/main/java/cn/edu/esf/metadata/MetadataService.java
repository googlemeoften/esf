package cn.edu.esf.metadata;

import cn.edu.esf.model.matedata.ServiceMetadata;

/**
 * 服务元数据服务
 * @Author heyong
 * @Date 2017/1/4
 */
public interface MetadataService {

    /**
     * 发布服务元数据信息
     *
     * @param metadata
     *            服务元数据对象
     */
    void publish(ServiceMetadata metadata);

    /**
     * 重新发布服务元数据信息
     *
     * @param metadata
     *            服务元数据对象，不可能为null
     * @return 是否重新发布成功
     */
    boolean republish(ServiceMetadata metadata);

    /**
     * 订阅服务元数据信息
     *
     * @param metadata
     *            服务元数据对象
     */
    void subscribe(ServiceMetadata metadata);


    /**
     * 反注册服务元数据信息
     *
     * @param metadata
     *            服务元数据对象
     */
    void unregister(ServiceMetadata metadata);
}
