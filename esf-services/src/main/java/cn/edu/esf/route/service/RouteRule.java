package cn.edu.esf.route.service;/**
 * Created by HeYong on 2017/2/13.
 */

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Description:ESF服务路由规则定义:规则的表现形式为一个地址过滤正则式列表。若一个地址满足列表中的某一个正则式，则会被选中，否则被过滤。
 *
 * @author heyong
 * @Date 2017/2/13
 */
public class RouteRule<M> {

    // 规则索引，主要为argsRule服务，存放了interfaceRule和methodRule中的规则
    private Map<? extends Object, ? extends List<String>> keyedRules;
    private Object interfaceRule;//接口级路由规则，keyedRules中的key
    private Map<M, Object> methodRule; // 方法级路由规则，keyedRules中的key
    private Map<M, Args2KeyCalculator> argsRule; // 参数级路由规则，calculate结果是keyedRules中的key

    /**
     * 路由规则结果为空时，是否生效
     */
    private boolean emptyProtection = false;

    // 如果ip不涉及正则，在匹配时可以直接startWith，避免正则的cpu消耗
    // 路由规则应该保持 172.23.18.19:* 以兼容老客户端
    // 另外兼容*表示全匹配
    private boolean isIpRegexOn = true;

    /**
     * 分隔符定义，及工具方法分隔符的定义<br />
     * 要求即能区分（不能用在类名定义中），又能给String.split(regex)方法直接使用而不用转化
     */
    public static final String METHOD_SIGS_JOINT_MARK = "#";

    public static final String joinMethodSigs(Method m) {
        StringBuilder sb = new StringBuilder(m.getName());
        Class<?>[] paramTypes = m.getParameterTypes();
        for (Class<?> c : paramTypes) {
            sb.append(METHOD_SIGS_JOINT_MARK).append(c.getName());
        }
        return sb.toString();
    }

    public static final String joinMethodSigs(String methodName, String[] paramTypeStrs) {
        StringBuilder sb = new StringBuilder(methodName);
        for (String type : paramTypeStrs) {
            sb.append(METHOD_SIGS_JOINT_MARK).append(type);
        }
        return sb.toString();
    }

    public Map<? extends Object, ? extends List<String>> getKeyedRules() {
        return keyedRules;
    }

    public void setKeyedRules(Map<? extends Object, ? extends List<String>> keyedRules) {
        this.keyedRules = keyedRules;
    }

    public Object getInterfaceRule() {
        return interfaceRule;
    }

    public void setInterfaceRule(Object interfaceRule) {
        this.interfaceRule = interfaceRule;
    }

    public Map<M, Object> getMethodRule() {
        return methodRule;
    }

    public void setMethodRule(Map<M, Object> methodRule) {
        this.methodRule = methodRule;
    }

    public Map<M, Args2KeyCalculator> getArgsRule() {
        return argsRule;
    }

    public void setArgsRule(Map<M, Args2KeyCalculator> argsRule) {
        this.argsRule = argsRule;
    }

    public boolean isEmptyProtection() {
        return emptyProtection;
    }

    public void setEmptyProtection(boolean emptyProtection) {
        this.emptyProtection = emptyProtection;
    }

    public boolean isIpRegexOn() {
        return isIpRegexOn;
    }

    public void setIpRegexOn(boolean ipRegexOn) {
        isIpRegexOn = ipRegexOn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append("{");
        sb.append("\nkeyedRules=").append(this.keyedRules);
        sb.append(",\ninterfaceRule=").append(this.interfaceRule);
        sb.append(",\nmethodRule=").append(this.methodRule);
        sb.append(",\nargsRule=").append(this.argsRule).append("\n}");
        return sb.toString();
    }
}
