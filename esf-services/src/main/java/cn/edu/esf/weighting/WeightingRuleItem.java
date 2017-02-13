package cn.edu.esf.weighting;/**
 * Created by HeYong on 2017/2/13.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/13
 */
public class WeightingRuleItem {
    private List<Pattern> ipPatterns = new ArrayList<>();
    private int weight = 1;

    public void addPattern(String pattern) {
        ipPatterns.add(Pattern.compile(pattern));
    }

    public List<Pattern> getIpPatterns() {
        return ipPatterns;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
