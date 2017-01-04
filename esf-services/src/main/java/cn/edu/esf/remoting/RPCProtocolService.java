package cn.edu.esf.remoting;

import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.exception.ESFException;
import cn.edu.esf.model.ServiceMetadata;

/**
 * RPC服务协议，负责最后远程调用
 *
 * @Author heyong
 * @Date 2017/1/4
 */
public interface RPCProtocolService {

    /**
     * 发起服务的调用
     *
     * @param request
     * @param metadata
     * @param targetURL
     * @return
     * @throws ESFException
     */
    public Object invoke(ESFRequest request, ServiceMetadata metadata, String targetURL) throws ESFException;


    /**
     * 服务的注册
     *
     * @param metadata
     * @throws ESFException
     */
    public void registerProvider(ServiceMetadata metadata) throws ESFException;


    /**
     * 验证服务地址是否有效
     * @param targetURL
     * @return
     */
    public boolean validTarget(String targetURL);
}
