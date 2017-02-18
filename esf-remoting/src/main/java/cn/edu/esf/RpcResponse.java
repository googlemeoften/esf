package cn.edu.esf;

import cn.edu.esf.domain.ESFResponse;
import cn.edu.esf.protocol.ByteBufferWrapper;
import cn.edu.esf.protocol.RPCProtocol;
import cn.edu.esf.serialize.JavaDecoder;
import cn.edu.esf.serialize.SerializeType;

/**
 * RPC远程调度协议（响应）
 * <p>
 * 1     标志ESF协议
 * 2     响应
 * 3     版本
 * 4     状态code
 * 5     序列化方式
 * 6~8   保留字节
 * 9~16  对应的请求ID
 * 17~19 返回值的长度大小
 * 不等  返回值的值
 *
 * @Author heyong
 * @Date 2016/12/15
 */
public class RpcResponse extends BaseResponse {
    private static final byte[] EXTENED_BYTES = new byte[3];
    private final byte[] response;
    private final byte codeType;

    public RpcResponse(long requestId, byte codeType, byte[] response) {
        super(RemotingConstants.PROCOCOL_VERSION_ESF_REMOTING, requestId);
        this.codeType = codeType;
        this.response = response;
    }

    public RpcResponse(long requestId, byte codeType, ResponseStatus status, byte[] response) {
        super(RemotingConstants.PROCOCOL_VERSION_ESF_REMOTING, requestId);
        this.codeType = codeType;
        this.response = response;
        this.setStatus(status);
    }

    public RpcResponse(long requestId, byte codeType, byte status, byte[] response) {
        super(RemotingConstants.PROCOCOL_VERSION_ESF_REMOTING, requestId);
        this.codeType = codeType;
        this.response = response;
        this.setStatus(status);
    }

    /***
     * 编码（不同的协议实现不同的编码）
     * @throws Exception

     * @param wrapper
     */
    @Override
    public void encode(ByteBufferWrapper wrapper) throws Exception {
        byte[] bady = this.getResponse();
        int capacity = RPCProtocol.RESPONSE_HEADER_LEN + bady.length;
        wrapper.ensureCapacity(capacity);
        wrapper.writeByte(RemotingConstants.PROCOCOL_VERSION_ESF_REMOTING)
                .writeByte(RPCProtocol.RESPONSE)
                .writeByte(RPCProtocol.VERSION)
                .writeByte(getStatus().getCode())
                .writeByte(codeType)//序列化方式
                .writeBytes(getExtenedBytes())
                .writeLong(this.getRequestID())
                .writeInt(bady.length)
                .writeBytes(bady);

    }

    @Override
    public Object getResponseObject(BaseRequest request) {
        ESFResponse response = new ESFResponse();
        switch (this.getStatus()) {
            case OK:
                try {
                    Object result = SerializeType.getDecoders(codeType).decode(this.getResponse());
                    response.setResponseObject(result);
                } catch (Exception e) {
                    response.setErrorMsg("Decoder fialed at client");
                    response.setResponseObject(e);
                }
                break;
            default:
                response.setResponseObject(new String(this.getResponse(), RemotingConstants.DEFAULT_CHARSET));
        }
        return response;
    }

    public byte getCodeType() {
        return codeType;
    }

    public byte[] getResponse() {
        return response;
    }
}
