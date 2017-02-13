package cn.edu.esf.tps;

import cn.edu.esf.domain.ESFRequest;

/**
 * TPS限流服务
 * @Author heyong
 * @Date 2017/1/4
 */
public interface TPSLimitService {
    /**
     * 限流服务是否有效
     * @return
     */
    boolean isValid();

    /**
     * 处理ESF请求，判断是否响应服务
     * @param hsfRequest
     * @return
     */
    TPSResult process(ESFRequest hsfRequest);
}
