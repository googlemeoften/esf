package cn.edu.esf.route.strategy.methodName;/**
 * Created by HeYong on 2017/2/13.
 */

import cn.edu.esf.route.service.RouteRule;
import cn.edu.esf.route.service.RouteRuleParser;
import cn.edu.esf.route.service.RouteRuleParserException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/13
 */
public class MethodNameRouteRuleParser implements RouteRuleParser {

    private static <M> String getMethodName(M m) {
        if (m instanceof Method) {
            return ((Method) m).getName();
        } else if (m instanceof String) {
            String[] methodSigs = ((String) m).split(RouteRule.METHOD_SIGS_JOINT_MARK);
            return methodSigs[0];
        } else {
            throw new IllegalArgumentException("无法识别的方法签名表示：" + m);
        }
    }



    @Override
    public <M> RouteRule<M> parse(Object rawRouteRuleObj, List<M> methodSigs) throws RouteRuleParserException {

        if (!Map.class.isAssignableFrom(rawRouteRuleObj.getClass())) {
            return null;
        }

        Map<? extends String, ? extends List<String>> methodRouteInfos;
        try {
            methodRouteInfos = (Map<? extends String, ? extends List<String>>) rawRouteRuleObj;
        } catch (ClassCastException e) {
            return null;
        }

        RouteRule<M> rr = new RouteRule<M>();
        rr.setKeyedRules(methodRouteInfos);

        Map<M, Object> methodRule = new HashMap<M, Object>();

        for (M m : methodSigs) {
            String methodName = getMethodName(m);
            if (methodRouteInfos.containsKey(methodName)) {
                methodRule.put(m, methodName);
            }
        }
        rr.setMethodRule(methodRule);

        return rr;
    }
}
