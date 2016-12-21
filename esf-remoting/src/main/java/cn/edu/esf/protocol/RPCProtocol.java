package cn.edu.esf.protocol;

import cn.edu.esf.RpcRequest;
import cn.edu.esf.RpcResponse;
import cn.edu.esf.utils.ThreadLocalCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public class RPCProtocol implements Protocol {
    private static final Logger LOGGER = LoggerFactory.getLogger(RPCProtocol.class);
    public static final int REQUEST_HEADER_LEN = 1 * 6 + 6 * 4;
    public static final int RESPONSE_HEADER_LEN = 1 * 8 + 4 * 3;
    public static final byte VERSION = (byte) 1;
    public static final byte REQUEST = (byte) 0;
    public static final byte RESPONSE = (byte) 1;

    /**
     * 将IO流转化为Object
     *
     * @param wrapper
     * @param originPos
     * @return
     * @throws Exception
     */
    @Override
    public Object decode(ByteBufferWrapper wrapper, int originPos) throws Exception {
        if (wrapper.readableBytes() < 2) {
            wrapper.setReaderIndex(originPos);
            return null;
        }

        byte type = wrapper.readByte();
        byte version = wrapper.readByte();

        if (version == VERSION) {
            if (type == REQUEST) {
                return decodeRequest(wrapper, originPos);
            } else if (type == RESPONSE) {
                return decodeResponse(wrapper, originPos);
            } else {
                LOGGER.error("protocol type:" + type + " is not support!!");
            }
        } else {
            LOGGER.error("procotol verion:" + version + " is not support!!");
        }
        return null;
    }

    /**
     * RPC远程调度协议（请求）
     * <p>
     * 1     请求协议类型（心跳/rpC）
     * 2     请求/响应
     * 3     协议版本
     * 4     序列化方式
     * 5~7   保留字节（后期扩展）
     * 8~15  请求ID
     * 16~19 请求超时
     * 20~35 服务名，方法名方法参数值的长度
     * 不等  服务名，方法名，方法参数的值
     * +4    附加信息的长度
     * 不等  附加信息的值
     *
     * @param wrapper
     * @param originPos
     * @return
     */
    private Object decodeRequest(ByteBufferWrapper wrapper, int originPos) {
        if (wrapper.readableBytes() < REQUEST_HEADER_LEN - 2) {
            wrapper.setReaderIndex(originPos);
            return null;
        }
        byte codecType = wrapper.readByte();
        wrapper.setReaderIndex(wrapper.readerIndex() + 3);
        long requestID = wrapper.readLong();
        int timeout = wrapper.readInt();
        int targetInstanceLen = wrapper.readInt();
        int methodNameLen = wrapper.readInt();
        int argCount = wrapper.readInt();
        int argInfoLen = argCount * 4 * 2;//参数的类型以及参数的对象的长度信息

        int exceptedInfoLen = targetInstanceLen + methodNameLen + argInfoLen + 4;
        if (wrapper.readableBytes() < exceptedInfoLen) {
            wrapper.setReaderIndex(originPos);
            return null;
        }

        int[] argTypesLen = new int[argCount];
        for (int i = 0; i < argCount; i++) {
            argTypesLen[i] = wrapper.readInt();
        }

        int[] argsLen = new int[argCount];
        for (int i = 0; i < argCount; i++) {
            argsLen[i] = wrapper.readInt();
        }
        int requestPropLen = wrapper.readInt();

        byte[] targetInstanceBytes = new byte[targetInstanceLen];
        wrapper.readBytes(targetInstanceBytes);
        byte[] methodNameBytes = new byte[methodNameLen];
        wrapper.readBytes(methodNameBytes);

        byte[][] argTypes = new byte[argCount][];
        for (int i = 0; i < argCount; i++) {
            byte[] argType = new byte[argTypesLen[i]];
            wrapper.readBytes(argType);
            argTypes[i] = argType;
        }

        byte[][] args = new byte[argCount][];
        for (int i = 0; i < argCount; i++) {
            byte[] arg = new byte[argsLen[i]];
            wrapper.readBytes(arg);
            args[i] = arg;
        }
        byte[] requestPropsBytes = new byte[requestPropLen];
        wrapper.readBytes(requestPropsBytes);

        String[] argTypesString = new String[argCount];
        for (int i = 0; i < argCount; i++) {
            argTypesString[i] = ThreadLocalCache.getString(argTypes[i]);
        }
        return new RpcRequest(requestID, timeout, ThreadLocalCache.getString(targetInstanceBytes),
                ThreadLocalCache.getString(methodNameBytes), argTypesString, args, requestPropsBytes, 0);
    }

    /**
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
     * @param wrapper
     * @param originPos
     * @return
     */
    private Object decodeResponse(ByteBufferWrapper wrapper, int originPos) {
        if (wrapper.readableBytes() < RESPONSE_HEADER_LEN - 2) {
            wrapper.setReaderIndex(originPos);
            return null;
        }

        byte status = wrapper.readByte();
        byte codecType = wrapper.readByte();
        wrapper.setReaderIndex(wrapper.readerIndex() + 3);
        long requestID = wrapper.readLong();
        int bodyLen = wrapper.readInt();
        if (wrapper.readableBytes() < bodyLen) {
            wrapper.setReaderIndex(originPos);
            return null;
        }
        byte[] body = new byte[bodyLen];
        wrapper.readBytes(body);
        return new RpcResponse(requestID, status, body);
    }
}
