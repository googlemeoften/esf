///**
// * High-Speed Service Framework (HSF)
// *
// * www.taobao.com
// *  (C) 淘宝(中国) 2003-2011
// */
//package cn.edu.esf.route.strategy.groovy;
//
//
//import cn.edu.esf.route.service.RouteRule;
//import cn.edu.esf.route.service.RouteRuleParser;
//import cn.edu.esf.route.service.RouteRuleParserException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.lang.reflect.Method;
//import java.util.List;
//
///**
// * 解析groovy脚本表示的路由规则
// * <ol>
// * <li>Map&lt;String, List&lt;String&gt;&gt; routingRuleMap()
// * <li>String interfaceRoutingRule()
// * <li>String mathodRoutingRule(String methodName, String[] paramTypeStrs)
// * <li>String argsRoutingRule(String methodName, String[] paramTypeStrs)
// *
// * @author linxuan
// */
//public class GroovyRouteRuleParser implements RouteRuleParser {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyRouteRuleParser.class);
//    public static final String PREFIX = "Groovy_v200907@";
//
//    /**
//     * @return Object[]{methodName, String[]{arg0.className, arg2.className ...}
//     *         } groovy.method(String methodName, String[] paramTypeStrs)
//     */
//    private static Object[] getArgs(Method m) {
//        Class<?>[] paramTypes = m.getParameterTypes();
//        Object[] paramTypeNames = new Object[paramTypes.length];
//        for (int k = 0; k < paramTypes.length; k++) {
//            paramTypeNames[k] = paramTypes[k].getName();
//        }
//        return new Object[] { m.getName(), paramTypeNames };
//    }
//
//    private static Method getMethod(Class<?> c, String name, Class<?>... parameterTypes)
//            throws RouteRuleParserException {
//        try {
//            return c.getMethod(name, parameterTypes);
//        } catch (SecurityException e) {
//            throw new RouteRuleParserException("获取方法失败。方法名：" + name, e);
//        } catch (NoSuchMethodException e) {
//            return null;
//        }
//    }
//
//    /**
//     * @return Object[]{methodName, String[]{arg0.className, arg2.className ...}
//     *         } groovy.method(String methodName, String[] paramTypeStrs)
//     */
//    private static <M> Object[] getMethodSigArgs(M m) {
//        if (m instanceof Method) {
//            return getArgs((Method) m);
//        } else if (m instanceof String) {
//            String[] methodSigs = ((String) m).split(RouteRule.METHOD_SIGS_JOINT_MARK);
//            String[] paramTypeNames = new String[methodSigs.length - 1];
//            System.arraycopy(methodSigs, 1, paramTypeNames, 0, paramTypeNames.length);
//            return new Object[] { methodSigs[0], paramTypeNames };
//        } else {
//            throw new IllegalArgumentException("无法识别的方法签名表示：" + m);
//        }
//    }
//
//    private static Object invoke(Object obj, Method m, Object... args) {
//        try {
//            return m.invoke(obj, args);
//        } catch (Throwable t) {
//            LOGGER.warn("调用方法：" + m + "失败", t);
//            return null;
//        }
//    }
//
//    public <M> RouteRule<M> parse(Object rawRouteRuleObj, List<M> methodSigs) throws RouteRuleParserException {
//        if (!(rawRouteRuleObj instanceof String)) {
//            // 交给其他解析器处理
//            return null;
//        }
//        String groovyRule = (String) rawRouteRuleObj;
//        if (!groovyRule.startsWith(PREFIX)) {
//            // 交给其他解析器处理
//            return null;
//        }
//
//        groovyRule = groovyRule.substring(PREFIX.length());
//        if (groovyRule.length() == 0) {
//            // 路由规则为空，当做没有路由规则，清空之前的路由规则
//            return null;
//        }
//
//        GroovyClassLoader loader = new GroovyClassLoader(GroovyRouteRuleParser.class.getClassLoader());
//        ClassLoader contextClassLoader = null;
//        Class<?> c_groovy = null;
//
//        try {
//            contextClassLoader = Thread.currentThread().getContextClassLoader();
//            if (contextClassLoader != null) {
//                Thread.currentThread().setContextClassLoader(null);
//            }
//            c_groovy = loader.parseClass(groovyRule);
//        } catch (CompilationFailedException e) {
//            throw new RouteRuleParserException("groovy 编译出错!", e);
//        } finally {
//            if (contextClassLoader != null) {
//                Thread.currentThread().setContextClassLoader(contextClassLoader);
//            }
//        }
//        if (c_groovy == null) {
//            return new RouteRule<M>();
//        }
//        Object ruleObj;
//        try {
//            ruleObj = c_groovy.newInstance();
//        } catch (Throwable t) {
//            throw new RouteRuleParserException("实例化路由规则对象失败", t);
//        }
//
//        /**
//         * 获得规则列表的map；key的取值和含义由配置者设计
//         */
//        RouteRule<M> rule = new RouteRule<M>();
//        Method m_routingRuleMap = getMethod(c_groovy, "routingRuleMap", new Class[0]);
//        if (m_routingRuleMap == null) {
//            LOGGER.warn("No routingRuleMap in groovy route rule");
//            return rule;
//        }
//        Object keyedRules = invoke(ruleObj, m_routingRuleMap);
//        if (keyedRules == null) {
//            LOGGER.warn("No routingRuleMap in groovy route rule");
//            return rule;
//        }
//        rule.setKeyedRules(cast2KeyedRules(keyedRules));
//
//        /**
//         * 获得接口级路由规则
//         */
//        Method m_interfaceRoutingRule = getMethod(c_groovy, "interfaceRoutingRule", new Class[0]);
//        if (m_interfaceRoutingRule != null) {
//            Object interfaceRoutingRule = invoke(ruleObj, m_interfaceRoutingRule);
//            if (interfaceRoutingRule != null) {
//                rule.setInterfaceRule(interfaceRoutingRule);
//            }
//        }
//
//        /**
//         * 获得方法级和参数级路由规则
//         */
//        Method m_mathodRoutingRule = getMethod(c_groovy, "mathodRoutingRule", new Class[] { String.class,
//                String[].class });
//        Method m_argsRoutingRule = getMethod(c_groovy, "argsRoutingRule", new Class[] { String.class, String[].class });
//
//        Map<M, Object> methodRule = new HashMap<M, Object>();
//        Map<M, Args2KeyCalculator> argsRule = new HashMap<M, Args2KeyCalculator>();
//
//        for (M m : methodSigs) {
//            Object[] args = getMethodSigArgs(m);
//            if (m_mathodRoutingRule != null) {
//                Object key4mathod = invoke(ruleObj, m_mathodRoutingRule, args);
//                if (key4mathod != null) {
//                    methodRule.put(m, key4mathod);
//                }
//            }
//            if (m_argsRoutingRule != null) {
//                Closure closure = (Closure) invoke(ruleObj, m_argsRoutingRule, args);
//                if (closure != null) {
//                    argsRule.put(m, new Args2KeyClosure(closure));
//                }
//            }
//        }
//        rule.setMethodRule(methodRule);
//        rule.setArgsRule(argsRule);
//
//        Method m_isIpRegexOn = getMethod(c_groovy, "isIpRegexOn", new Class[0]);
//        Object isIpRegexOn = true;
//        if (m_isIpRegexOn != null) {
//            isIpRegexOn = invoke(ruleObj, m_isIpRegexOn);
//            LOGGER.warn("isIpRegexOn : " + isIpRegexOn);
//        }
//        rule.setIpRegexOn((Boolean) isIpRegexOn);
//
//        Method m_Protect = getMethod(c_groovy, "isEmptyProtection", new Class[0]);
//        Object isEmptyProtection = false;
//        if (m_Protect == null) {
//            LOGGER.warn("No isEmptyProtection in groovy route rule");
//        } else {
//            isEmptyProtection = invoke(ruleObj, m_Protect);
//        }
//        rule.setEmptyProtection((Boolean) isEmptyProtection);
//
//        LOGGER.info("Parse route rule successed, RouteRule:" + rule);
//        return rule;
//    }
//
//    @SuppressWarnings("unchecked")
//    private Map<? extends Object, ? extends List<String>> cast2KeyedRules(Object keyedRules)
//            throws RouteRuleParserException {
//        try {
//            return (Map<? extends Object, ? extends List<String>>) keyedRules;
//        } catch (ClassCastException e) {
//            throw new RouteRuleParserException(
//                    "Groovy脚本routingRuleMap方法返回类型不是Map<? extends Object, ? extends List<String>>", e);
//        }
//    }
//}
