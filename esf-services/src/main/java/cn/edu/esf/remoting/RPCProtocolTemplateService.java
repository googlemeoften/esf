package cn.edu.esf.remoting;/**
 * Created by HeYong on 2017/2/13.
 */

import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.exception.ESFException;
import cn.edu.esf.model.matedata.ServiceMetadata;

import java.lang.reflect.Method;

/**
 * Description:RPC协议模板服务，提供一些RPC协议的公用实现
 *
 * @author heyong
 * @Date 2017/2/13
 */
public interface RPCProtocolTemplateService {
    /**
     * 基于非反射方式调用ESF服务
     *
     * @param protocol
     *            RPC协议
     * @param metadata
     *            服务元信息对象
     * @param methodName
     *            需要调用的方法名
     * @param parameterTypes
     *            方法参数类型
     * @param args
     *            调用方法的参数
     * @return Object 远程ESF服务执行后的响应对象
     *
     * @throws ESFException
     *             调用远程服务时出现超时、网络、业务异常时抛出
     * @throws Throwable
     *             业务异常Exception或严重错误Error
     */
    Object invokeWithMethodInfos(String protocol, ServiceMetadata metadata, String methodName, String[] parameterTypes,
                                 Object[] args) throws ESFException, Throwable;

    /**
     * 基于反射方式调用ESF服务
     *
     * @param protocol
     *            RPC协议
     * @param metadata
     *            服务元信息对象
     * @param method
     *            需要调用的服务的方法对象
     * @param args
     *            调用方法的参数
     * @return Object 远程ESF服务执行后的响应对象
     *
     * @throws ESFException
     *             调用远程服务时出现超时、网络、业务异常时抛出
     * @throws Throwable
     *             业务异常Exception或严重错误Error
     */
    public Object invokeWithMethodObject(String protocol, ServiceMetadata metadata, Method method, String methodName,
                                         Class<?>[] parameterTypes, Object[] args) throws ESFException, Throwable;

    /**
     * 判断本次调用是否需要目标地址
     *
     * @param metadata
     *            服务元信息对象
     * @param request
     *            ESF请求对象
     * @return 需要目标地址的情况下，返回<tt>ture</tt>
     */
    boolean isNeedTarget(String protocol, ServiceMetadata metadata, ESFRequest request);

    /**
     * 注册服务提供者
     *
     * @param protocol
     *            RPC协议
     * @param metadata
     *            服务元信息
     * @throws ESFException
     */
    void registerProvider(String protocol, ServiceMetadata metadata) throws ESFException;

    /**
     * 校验目标地址的可用性<br />
     * 服务不可用的情况下，调用逻辑中将自动进行其他的选址动作
     *
     * @param targetURL
     *            服务目标地址
     * @return 目标地址是否可用
     */
    boolean validTarget(String protocol, String targetURL);
}
