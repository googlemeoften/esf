package cn.edu.esf.tps;

import java.io.Serializable;
/**
 * TPS限流结果
 * @Author heyong
 * @Date 2017/1/4
 */
public class TPSResult implements Serializable {

    private static final long serialVersionUID = 6400948934979467890L;

    private boolean allowed = true;

    private String message;

    public String getMessage() {
        return message;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TPSResult [allowed=" + allowed + ", message=" + message + "]";
    }
}
