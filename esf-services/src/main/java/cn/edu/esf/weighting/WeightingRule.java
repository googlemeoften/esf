package cn.edu.esf.weighting;

import cn.edu.esf.rule.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/13
 */
public class WeightingRule implements Rule {
    private final List<WeightingRuleItem> items = new ArrayList<>();
    private long expireTime = System.currentTimeMillis() + 3600 * 1000;
    private final String rawRule;

    public WeightingRule(String rawRule) {
        this.rawRule = rawRule;
    }

    public void addItem(WeightingRuleItem item) {
        items.add(item);
    }

    ;

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }


    @Override
    public String getRuleName() {
        return "weightingRule";
    }

    @Override
    public String getRawRule() {
        return rawRule;
    }

    @Override
    public boolean validate() {
        return true;
    }

    public int getWeight(String IP) {
        if (this.expireTime > System.currentTimeMillis()) {
            for (WeightingRuleItem item : items) {
                for (Pattern p : item.getIpPatterns()) {
                    if (p.matcher(IP).matches()) {
                        return item.getWeight();
                    }
                }
            }
        }
        return 1;
    }
}
