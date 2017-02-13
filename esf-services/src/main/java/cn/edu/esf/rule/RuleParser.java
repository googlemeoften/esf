package cn.edu.esf.rule;/**
 * Created by HeYong on 2017/2/13.
 */

/**
 * Description: 规则解析器
 *
 * @author heyong
 * @Date 2017/2/13
 */
public interface RuleParser {

    /**
     * 判断解析器是否支持参数传入的规则
     * @param rawRule
     * @return
     */
    boolean accept(String rawRule);

    /**
     * 规则解析器所支持的规则名称。该名称作为规则的头，与规则正文之间使用@分隔
     * @return
     */
    String getRuleName();

    /**
     * 解析規則，获得规则对象
     * @param rawRule
     * @return
     * @throws RuleParseException
     */
    Rule parse(String rawRule) throws RuleParseException;
}
