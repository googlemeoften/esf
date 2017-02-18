package cn.edu.esf.server;

import cn.edu.esf.*;
import cn.edu.esf.pool.ThreadPoolManager;
import cn.edu.esf.server.output.RPCServerOutput;
import cn.edu.esf.utils.RemotingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * RPC
 * @Author heyong
 * @Date 2017/1/2
 */
public class RPCProtocolHandler implements ProtocolHandler<RpcRequest> {
    private Logger LOGGER = LoggerFactory.getLogger(RPCProtocolHandler.class);
    private final RpcRequestProcessor rpcRequestProcessor;
    private final ThreadPoolManager threadPoolManager;

    public RPCProtocolHandler(RpcRequestProcessor rpcRequestProcessor) {
        this.rpcRequestProcessor = rpcRequestProcessor;
        this.threadPoolManager = rpcRequestProcessor.getThreadPoolManager();
    }

    @Override
    public void handleRequest(RpcRequest request, Connection connection, long startTime) {
        try {
            rpcRequestProcessor.handleRequest(RemotingUtil.convert(request), new RPCServerOutput(connection,request,startTime));

        } catch (Throwable t) {
            LOGGER.error("Provider Error:" + connection);
            byte[] exceptionBytes = "ESF 业务问题，请检查日志".getBytes();
            connection.writeResponseToChannel(new RpcResponse(request.getRequestID(),
                    (byte) request.getProtocolType(), ResponseStatus.SERVER_ERROR, exceptionBytes));

        }
    }

    @Override
    public Executor getExecutor(RpcRequest request) {
        return threadPoolManager.getExecutor(request.getTargetInstance());
    }
}
