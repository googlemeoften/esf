package cn.edu.esf.exception;

/**
 * @Author heyong
 * @Date 2017/1/4
 */
public class ESFException extends Exception {
    private static final long serialVersionUID = -7599768256906262049L;
    private static final String lineSeparator = System.getProperty("line.separator");

    private final String errorCode;
    private final String desc;

    public ESFException(String errorCode) {
        this(errorCode, "");
    }

    public ESFException(String errorCode, String desc) {
        super(errorCode);
        this.errorCode = errorCode;
        this.desc = desc;
    }

    public ESFException(String errorCode, String desc, Throwable t) {
        super(desc, t);
        this.errorCode = errorCode;
        this.desc = desc;
    }

    public ESFException(String errorCode, Throwable t) {
        this(errorCode, null, t);
    }

    @Override
    public String toString() {
        if (desc == null) {
            return errorCode;
        }
        StringBuilder sb = new StringBuilder(errorCode);
        sb.append(lineSeparator);
        sb.append("描述信息：").append(desc);
        return sb.toString();
    }
}
