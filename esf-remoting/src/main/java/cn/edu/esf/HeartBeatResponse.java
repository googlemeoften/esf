package cn.edu.esf;

import cn.edu.esf.protocol.ByteBufferWrapper;
import cn.edu.esf.protocol.HeartBeatProtocol;

/**
 * 心跳协议：响应
 *  协议类型
 *  请求/响应
 *  协议版本
 *  扩展字段（3 byte）
 *  请求ID
 *  填充字段
 *
 * @Author heyong
 * @Date 2016/12/15
 */
public class HeartBeatResponse extends BaseResponse {

    public HeartBeatResponse(long requestID) {
        super(RemotingConstants.PROCOCOL_VERSION_HEATBEAT, requestID);
    }


    @Override
    public Object getResponseObject(BaseRequest request) {
        return null;
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
                .writeByte(HeartBeatProtocol.RESPONSE)
                .writeByte(HeartBeatProtocol.VERSION)
                .writeBytes(getExtenedBytes())
                .writeLong(this.getRequestID())
                .writeInt(0);
    }
}
