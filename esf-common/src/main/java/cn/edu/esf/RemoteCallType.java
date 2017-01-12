package cn.edu.esf;

/**
 * 调用方式
 */
public enum RemoteCallType {

    FUTURE("future"), CALLBACK("callback");

    private String type;

    private RemoteCallType(String type) {
        this.type = type;
    }

    public String getCallType() {
        return type;
    }
}