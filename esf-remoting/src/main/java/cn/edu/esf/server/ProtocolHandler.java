package cn.edu.esf.server;

import cn.edu.esf.BaseRequest;
import cn.edu.esf.Connection;

import java.util.concurrent.Executor;

/**
 * 不同协议有不同处理者（心跳/RPC）
 *
 * @Author heyong
 * @Date 2016/12/15
 */
public interface ProtocolHandler<T extends BaseRequest> {
    public void handleRequest(final T request, final Connection connection, final long startTime);

    public Executor getExecutor(final T request);
}
