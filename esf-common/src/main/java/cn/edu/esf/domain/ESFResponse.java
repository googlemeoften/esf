package cn.edu.esf.domain;

import java.io.Serializable;

/**
 * @Author heyong
 * @Date 2016/12/20
 */
public class ESFResponse implements Serializable {
    private static final long serialVersionUID = 1987659854993608908L;
    private String errorMsg;
    private Object responseObject;
    private boolean isError = false;
    private transient String errorType;

    public Object getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(Object responseObject) {
        this.responseObject = responseObject;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        this.isError = true;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    @Override
    public String toString() {
        return "ESFResponse{" +
                "errorMsg='" + errorMsg + '\'' +
                ", responseObject=" + responseObject +
                ", isError=" + isError +
                ", errorType='" + errorType + '\'' +
                '}';
    }
}
