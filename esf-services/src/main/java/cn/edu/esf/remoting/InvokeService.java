package cn.edu.esf.remoting;

import cn.edu.esf.RemotingURL;
import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.exception.ESFException;
import cn.edu.esf.model.ServiceMetadata;

/**
 * @Author heyong
 * @Date 2017/1/4
 */
public interface InvokeService {

    public static final String INVOKE_REMOTING_SYNC = "SYNC";

    /**
     * 调用ESF服务
     * @param request
     * @param metadata
     * @param targetURL 服务提供者地址
     * @param codecType 序列化方式
     * @param timeout
     * @return
     * @throws ESFException
     */
    public Object invoke(ESFRequest request, ServiceMetadata metadata, RemotingURL targetURL, byte codecType, int timeout) throws ESFException;
}
