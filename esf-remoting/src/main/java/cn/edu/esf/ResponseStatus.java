package cn.edu.esf;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public enum ResponseStatus {
    OK(20, "SUCCESS"),
    CLIENT_TIMEOUT(40, "timeout"),
    CLIENT_ERROR(41," client error"),
    SERVER_ERROR(50, "server error"),
    UNKNOW_CODE(100, "Unkonw code");

    private final byte code;
    private final String message;

    ResponseStatus(final int code, final String message) {
        this.code = (byte) code;
        this.message = message;
    }

    public static ResponseStatus fromCode(byte code) {
        ResponseStatus[] values = ResponseStatus.values();
        for (ResponseStatus status : values) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return UNKNOW_CODE;
    }

    public byte getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
