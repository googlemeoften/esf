package cn.edu.esf.protocol;

import cn.edu.esf.RemotingConstants;

/**
 * @Author heyong
 * @Date 2016/12/20
 */
public final class ProtocolFactory {
    private final Protocol[] protocolHandlers = new Protocol[256];
//    private final ServerHandler<?>[] serverHandlers = new ServerHandler<?>[256];

    public static ProtocolFactory instance = new ProtocolFactory();

    private ProtocolFactory() {
        registerProtocol(RemotingConstants.PROCOCOL_VERSION_ESF_REMOTING, new RPCProtocol());
        registerProtocol(RemotingConstants.PROCOCOL_VERSION_HEATBEAT, new HeartBeatProtocol());
//        initServerSide(null);
    }

//    public void initServerSide(final RpcRequestProcessor rpcRequestProcessor) {
//        registerServerHandler(RemotingConstants.PROCOCOL_VERSION_ESF_REMOTING,
//                new RPCServerHandler(rpcRequestProcessor));
//        registerServerHandler(RemotingConstants.PROCOCOL_VERSION_HEATBEAT, new HeartBeatServerHandler());
//        registerServerHandler(RemotingConstants.PROCOCOL_VERSION_TB_REMOTING,
//                new TbRemotingHandler(rpcRequestProcessor));
//        registerServerHandler(RemotingConstants.PROCOCOL_VERSION_DUBBO_REMOTING, new DubboRemotingHandler(
//                rpcRequestProcessor));
//    }

    private void registerProtocol(byte type, Protocol customProtocol) {
        if (protocolHandlers[type & 0x0FF] != null) {
            throw new RuntimeException("protocol header's sign is overlapped");
        }
        protocolHandlers[type & 0x0FF] = customProtocol;
    }

//    private void registerServerHandler(byte type, final ServerHandler<?> customServerHandler) {
//        serverHandlers[type & 0x0FF] = customServerHandler;
//    }

    public Protocol getProtocol(byte type) {
        return protocolHandlers[type & 0x0FF];
    }

//    public ServerHandler<?> getServerHandler(byte type) {
//        return serverHandlers[type & 0x0FF];
//    }

}