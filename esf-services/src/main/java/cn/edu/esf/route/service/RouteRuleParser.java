package cn.edu.esf.route.service;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 路由规则解析器
 *
 * @author heyong
 * @Date 2017/2/13
 */
public interface RouteRuleParser {

    /**
     * 如果不属于本Parser解析的格式，返回null；如果解析失败抛出异常
     * 
     * @param <M>
     *            代表方法签名的对象。 <br />
     *            可以是{@link Method}对象，也可以是格式为：方法名#参数类型1..._参数类型n的字符串
     * @param rawRouteRuleObj
     *            服务路由规则对象
     * @param methodSigs
     *            方法签名列表
     * @return RoutingRule对象
     */
    <M> RouteRule<M> parse(Object rawRouteRuleObj, List<M> methodSigs) throws RouteRuleParserException;
}
