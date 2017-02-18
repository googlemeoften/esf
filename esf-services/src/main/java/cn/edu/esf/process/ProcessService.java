package cn.edu.esf.process;

import cn.edu.esf.exception.ESFException;
import cn.edu.esf.model.matedata.ServiceMetadata;

/**
 * 服务的发布与服务的消费
 *
 * @Author heyong
 * @Date 2017/1/4
 */
public interface ProcessService {

    /**
     * 生成远程调用ESF服务的代理
     * 该代理的效果是生成ServiceMetadata里面interface接口的代理，调用时可将代理转型为服务接口，并进行直接的对象调用
     *
     * @param metaDate 服务元数据
     * @return
     * @throws ESFException 代理错误
     */
    public Object consume(ServiceMetadata metaDate) throws ESFException;

    /**
     * 对外发布ESF服务，将服务地址注册到注册中心
     * @param metadata
     * @throws ESFException
     */
    public void publis(ServiceMetadata metadata) throws ESFException;
}
