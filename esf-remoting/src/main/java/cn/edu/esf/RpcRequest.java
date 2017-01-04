package cn.edu.esf;

import cn.edu.esf.protocol.ByteBufferWrapper;
import cn.edu.esf.protocol.RPCProtocol;
import cn.edu.esf.server.ProtocolHandler;
import cn.edu.esf.utils.ThreadLocalCache;
import cn.edu.esf.utils.UUIDGenerator;

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
 * @Author heyong
 * @Date 2016/12/15
 */
public class RpcRequest extends BaseRequest {
    /**
     * =================================
     */
    private final static ProtocolHandler<? extends BaseRequest> protocolHandler = null;
    private final String targetInstance;
    private final String methodName;
    private final String[] argTypes;
    private final byte[][] requestObjects;
    private final byte[] requestProps;
    private final byte codecType;
//    private final int size;

    public RpcRequest(int timeout, String targetInstance, String methodName, String[] argTypes,
                      byte[][] requestObjects, byte[] requestProps, byte codecType) {
        this(UUIDGenerator.getNextOpaque(), timeout, targetInstance, methodName, argTypes, requestObjects, requestProps, codecType);
    }

    public RpcRequest(long requestID, int timeout, String targetInstance, String methodName, String[] argTypes,
                      byte[][] requestObjects, byte[] requestProps, byte codecType) {
        super(RemotingConstants.PROCOCOL_VERSION_ESF_REMOTING, requestID, timeout);
        this.targetInstance = targetInstance;
        this.methodName = methodName;
        this.argTypes = argTypes;
        this.requestObjects = requestObjects;
        this.requestProps = requestProps;
        this.codecType = codecType;
    }


    /***
     * 编码（不同的协议实现不同的编码）
     * @throws Exception

     * @param wrapper
     */
    @Override
    public void encode(ByteBufferWrapper wrapper) throws Exception {
        int requestArgTypesLength = 0;
        int requestArgsLength = 0;

        byte[][] requestArgTypes = new byte[argTypes.length][];
        for (int i = 0; i < argTypes.length; i++) {
            requestArgTypes[i] = ThreadLocalCache.getBytes(argTypes[i]);
            requestArgTypesLength += requestArgTypes[i].length;
        }

        if (requestObjects != null) {
            for (byte[] requestObject : requestObjects) {
                requestArgsLength += requestObject.length;
            }
        }

        byte[] targetInstanceBytes = ThreadLocalCache.getBytes(targetInstance);
        byte[] methodNameBytes = ThreadLocalCache.getBytes(methodName);
        long requestID = this.getRequestID();
        int timeout = this.getTimeout();
        int requestArgTypesCount = argTypes.length;
        int requestPropLength = requestProps == null ? 0 : requestProps.length;
        int capacity = RPCProtocol.REQUEST_HEADER_LEN + requestArgTypesCount * 4 * 2 + 5
                + targetInstanceBytes.length + methodNameBytes.length + requestArgTypesLength + requestArgsLength
                + requestPropLength;

        wrapper.ensureCapacity(capacity);
        wrapper.writeByte(RemotingConstants.PROCOCOL_VERSION_ESF_REMOTING)
                .writeByte(RPCProtocol.REQUEST)
                .writeByte(RPCProtocol.VERSION)
                .writeByte((byte) 0)//序列化版本
                .writeBytes(getExtenedBytes())
                .writeLong(requestID)
                .writeInt(timeout)
                .writeInt(targetInstanceBytes.length)
                .writeInt(methodNameBytes.length)
                .writeInt(requestArgTypesCount);
        for (byte[] requestType : requestArgTypes) {
            wrapper.writeInt(requestType.length);
        }
        if (requestObjects != null) {
            for (byte[] reuestObject : requestObjects) {
                wrapper.writeInt(reuestObject.length);
            }
        }
        wrapper.writeInt(requestPropLength);
        wrapper.writeBytes(targetInstanceBytes)
                .writeBytes(methodNameBytes);
        for (byte[] requestType : requestArgTypes) {
            wrapper.writeBytes(requestType);
        }
        if (requestObjects != null) {
            for (byte[] requestObject : requestObjects) {
                wrapper.writeBytes(requestObject);
            }
        }

        if (requestProps != null) {
            wrapper.writeBytes(requestProps);
        }

    }

    @Override
    public BaseResponse createErrorResponse(String errorInfo) {
        return new RpcResponse(this.getRequestID(), codecType, errorInfo.getBytes(RemotingConstants.DEFAULT_CHARSET));
    }

    @Override
    public ProtocolHandler<? extends BaseRequest> getProcotolHandler() {
        return protocolHandler;
    }

    public String getTargetInstance() {
        return targetInstance;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getArgTypes() {
        return argTypes;
    }

    public byte[][] getRequestObjects() {
        return requestObjects;
    }

    public byte[] getRequestProps() {
        return requestProps;
    }
}
