package cn.edu.esf.rule;/**
 * Created by HeYong on 2017/2/13.
 */

import cn.edu.esf.exception.ESFException;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/13
 */
public class RuleParseException extends ESFException {
    private static final long serialVersionUID = -2976178759025177293L;
    private static final String PREFIX = RuleParseException.class.getSimpleName() + "-";

    public RuleParseException(String errorCode, String msg) {
        super(PREFIX + errorCode, msg);
    }

    public RuleParseException(String errorCode, String msg, Throwable t) {
        super(PREFIX + errorCode, msg, t);
    }

    public RuleParseException(String errorCode, Throwable t) {
        super(PREFIX + errorCode, t);
    }
}
