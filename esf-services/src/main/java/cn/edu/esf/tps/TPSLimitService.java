package cn.edu.esf.tps;

import cn.edu.esf.domain.ESFRequest;

/**
 * TPS限流服务
 * @Author heyong
 * @Date 2017/1/4
 */
public interface TPSLimitService {

    boolean isValid();

    TPSResult process(ESFRequest hsfRequest);
}
