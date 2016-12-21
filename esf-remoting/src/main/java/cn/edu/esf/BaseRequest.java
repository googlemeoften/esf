package cn.edu.esf;

import cn.edu.esf.server.ProtocolHandler;
import cn.edu.esf.utils.UUIDGenerator;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public abstract class BaseRequest extends BaseHeader {
    private final int timeout;

    public BaseRequest(int protocolType) {
        this(protocolType, 0);
    }

    public BaseRequest(int protocolType, int timeout) {
        this(protocolType, UUIDGenerator.getNextOpaque(), timeout);

    }

    public BaseRequest(int protocolType, long requestID, int timeout) {
        super(protocolType, requestID);
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public abstract BaseResponse createErrorResponse(final String errorInfo);

    public abstract ProtocolHandler<? extends BaseRequest> getProcotolHandler();

}
