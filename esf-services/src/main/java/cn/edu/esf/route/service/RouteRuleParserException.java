package cn.edu.esf.route.service;

public class RouteRuleParserException extends Exception {

    private static final long serialVersionUID = -3350675256182096339L;

    public RouteRuleParserException() {
        super();
    }

    public RouteRuleParserException(String message) {
        super(message);
    }

    public RouteRuleParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
