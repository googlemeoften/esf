package cn.edu.esf.server;

import cn.edu.esf.Connection;
import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.domain.ESFResponse;
import cn.edu.esf.pool.ThreadPoolManager;
import cn.edu.esf.server.output.ServerOutput;

import java.util.concurrent.Executor;

/**
 * @Author heyong
 * @Date 2017/1/2
 */
public interface RpcRequestProcessor {

    /**
     * 业务线程的处理逻辑入口
     * @param appRequest
     * @param output
     * @return
     */
    public void handleRequest(ESFRequest appRequest, ServerOutput output  );

    /**
     * 根据请求 获取 业务线程池
     *
     * @param serviceName
     * @return
     */
    public Executor getExecutor(final String serviceName);

    public ThreadPoolManager getThreadPoolManager();
}
