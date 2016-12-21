package cn.edu.esf.protocol;

import cn.edu.esf.HeartBeatRequest;
import cn.edu.esf.HeartBeatResponse;
import cn.edu.esf.RemotingConstants;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public class HeartBeatProtocol implements Protocol {
    public static final byte PROTOCOL_HEARTBEAT = RemotingConstants.PROCOCOL_VERSION_HEATBEAT;
    public static final int CUSTOMPROTOCOL_HEADER_LEN = 1 * 6 + 3 * 4;
    public static final byte VERSION = (byte) 1;
    public static final byte REQUEST = (byte) 0;
    public static final byte RESPONSE = (byte) 1;

    @Override
    public Object decode(ByteBufferWrapper wrapper, int originPos) throws Exception {
        if (wrapper.readableBytes() < CUSTOMPROTOCOL_HEADER_LEN - 1) {
            wrapper.setReaderIndex(originPos);
            return null;
        }
        byte type = wrapper.readByte();
        byte version = wrapper.readByte();

        if (version == VERSION) {
            if (type == REQUEST) {
                wrapper.readByte();
                wrapper.readByte();
                wrapper.readByte();
                long requestID = wrapper.readLong();
                int timeout = wrapper.readInt();

                HeartBeatRequest request = new HeartBeatRequest(requestID, timeout);
                return request;
            } else if (type == RESPONSE) {
                wrapper.readByte();
                wrapper.readByte();
                wrapper.readByte();
                long requestID = wrapper.readLong();
                wrapper.readInt();
                HeartBeatResponse response = new HeartBeatResponse(requestID);
                return response;
            } else {
                throw new Exception("protocol type :" + type + " is not supported!");
            }
        } else {
            throw new Exception("protocol version :" + version + " is not supported!");
        }
    }
}
