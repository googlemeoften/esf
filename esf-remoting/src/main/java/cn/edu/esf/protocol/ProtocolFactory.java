package cn.edu.esf.protocol;

import cn.edu.esf.RemotingConstants;
import cn.edu.esf.server.HeartBeatPotocolHandler;
import cn.edu.esf.server.ProtocolHandler;
import cn.edu.esf.server.RPCProtocolHandler;
import cn.edu.esf.server.RpcRequestProcessor;

/**
 * @Author heyong
 * @Date 2016/12/20
 */
public final class ProtocolFactory {
    private final Protocol[] protocolHandlers = new Protocol[256];
    private final ProtocolHandler<?>[] serverHandlers = new ProtocolHandler<?>[256];

    public static ProtocolFactory instance = new ProtocolFactory();

    private ProtocolFactory() {
        registerProtocol(RemotingConstants.PROCOCOL_VERSION_ESF_REMOTING, new RPCProtocol());
        registerProtocol(RemotingConstants.PROCOCOL_VERSION_HEATBEAT, new HeartBeatProtocol());
    }

    public void initServerSide(final RpcRequestProcessor rpcRequestProcessor) {
        registerServerHandler(RemotingConstants.PROCOCOL_VERSION_ESF_REMOTING,
                new RPCProtocolHandler(rpcRequestProcessor));
        registerServerHandler(RemotingConstants.PROCOCOL_VERSION_HEATBEAT, new HeartBeatPotocolHandler());
    }

    private void registerProtocol(byte type, Protocol customProtocol) {
        if (protocolHandlers[type & 0x0FF] != null) {
            throw new RuntimeException("protocol header's sign is overlapped");
        }
        protocolHandlers[type & 0x0FF] = customProtocol;
    }

    private void registerServerHandler(byte type, final ProtocolHandler<?> customServerHandler) {
        serverHandlers[type & 0x0FF] = customServerHandler;
    }

    public Protocol getProtocol(byte type) {
        return protocolHandlers[type & 0x0FF];
    }

    public ProtocolHandler<?> getServerHandler(byte type) {
        return serverHandlers[type & 0x0FF];
    }

}