package cn.edu.esf.route.service;

/**
 * Description:将具体的参数映射为一个key的接口
 *
 * @author heyong
 * @Date 2017/2/13
 */
public interface Args2KeyCalculator {

    Object calculate(Object[] args);
}
