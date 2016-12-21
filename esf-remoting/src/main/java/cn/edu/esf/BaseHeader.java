package cn.edu.esf;

import cn.edu.esf.protocol.ByteBufferWrapper;

/**
 * 请求协议和响应协议的公共部分
 *
 * @Author heyong
 * @Date 2016/12/15
 */
public abstract class BaseHeader {

    /**
     * 协议类型（心跳协议/RPC协议）
     */
    private final int protocolType;

    /**
     * 请求ID
     */
    private final long requestID;

    /**
     * 填充字段
     */
    private static final byte[] EXTENED_BYTES = new byte[3];

    public BaseHeader(int protocolType, long requestID) {
        this.protocolType = protocolType;
        this.requestID = requestID;
    }

    public int getProtocolType() {
        return protocolType;
    }

    public long getRequestID() {
        return requestID;
    }

    public static byte[] getExtenedBytes() {
        return EXTENED_BYTES;
    }

    /***
     * 编码（不同的协议实现不同的编码）
     * @throws Exception
     */
    public abstract void encode(ByteBufferWrapper wrapper) throws Exception;
}
