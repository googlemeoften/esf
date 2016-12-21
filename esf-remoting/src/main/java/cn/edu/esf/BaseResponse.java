package cn.edu.esf;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public abstract class BaseResponse extends BaseHeader {
    private ResponseStatus status = ResponseStatus.OK;

    public BaseResponse(int protocolType, long requestID) {
        super(protocolType, requestID);
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public void setStatus(byte status) {
        this.status = ResponseStatus.fromCode(status);
    }

    @Override
    public String toString() {
        return this.getProtocolType() + ":" + status;
    }

    public abstract Object getResponseObject(BaseRequest request);
}
