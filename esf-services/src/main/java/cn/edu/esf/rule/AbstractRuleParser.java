package cn.edu.esf.rule;/**
 * Created by HeYong on 2017/2/13.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/13
 */
public abstract class AbstractRuleParser implements RuleParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRuleParser.class);

    @Override
    public boolean accept(String rawRule) {
        if(rawRule == null||rawRule.trim().length() == 0){
            return false;
        }

        int end = getRuleName().length() + 1;
        String header = rawRule.trim().substring(0,end);
        if((getRuleName()+"@").equals(header)){
            return true;
        }

        return false;
    }

    public abstract String getRuleName();

    public abstract Rule parse(String rawRule) throws RuleParseException ;
}
