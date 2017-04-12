package cn.edu.esf;

import cn.edu.esf.protocol.ByteBufferWrapper;
import cn.edu.esf.protocol.HeartBeatProtocol;
import cn.edu.esf.protocol.ProtocolFactory;
import cn.edu.esf.server.ProtocolHandler;

/**
 * 心跳协议:请求
 * 协议类型
 * 请求/响应
 * 协议版本
 * 扩展字段（3 byte）
 * 请求ID
 * 超时时间
 *
 * @Author heyong
 * @Date 2016/12/15
 */
public class HeartBeatRequest extends BaseRequest {

    private final static ProtocolHandler<? extends BaseRequest> protocolHandler =
            ProtocolFactory.instance.getServerHandler(RemotingConstants.PROCOCOL_VERSION_HEATBEAT);

    public HeartBeatRequest() {
        super(RemotingConstants.PROCOCOL_VERSION_HEATBEAT);
    }

    public HeartBeatRequest(long requestID, int timeout) {
        super(RemotingConstants.PROCOCOL_VERSION_HEATBEAT, requestID, timeout);
    }


    @Override
    public BaseResponse createErrorResponse(String errorInfo) {
        return new HeartBeatResponse(this.getRequestID());
    }

    @Override
    public ProtocolHandler<? extends BaseRequest> getProcotolHandler() {
        return protocolHandler;
    }

    /***
     * 编码（不同的协议实现不同的编码）
     * @throws Exception

     * @param wrapper
     */
    @Override
    public void encode(ByteBufferWrapper wrapper) throws Exception {
        wrapper.ensureCapacity(HeartBeatProtocol.CUSTOMPROTOCOL_HEADER_LEN);
        wrapper.writeByte(HeartBeatProtocol.PROTOCOL_HEARTBEAT)
                .writeByte(HeartBeatProtocol.REQUEST)
                .writeByte(HeartBeatProtocol.VERSION)
                .writeBytes(getExtenedBytes())
                .writeLong(this.getRequestID())
                .writeInt(this.getTimeout());
    }
}
