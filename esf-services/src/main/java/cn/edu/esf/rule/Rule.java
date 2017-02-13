package cn.edu.esf.rule;/**
 * Created by HeYong on 2017/2/13.
 */

/**
 * Description:ESF规则定义，所有用户定义的ESF规则数据将最终被解析为{@link Rule}对象。
 *
 * @author heyong
 * @Date 2017/2/13
 */
public interface Rule {

    /**
     * 规则名称
     */
    public String getRuleName();

    /**
     * 原始规则：从配置中心获得，未经处理的规则
     * @return
     */
    public String getRawRule();

    /**
     * 验证规则是否有效
     */
    public boolean validate();

}
