package cn.edu.esf.tps.component;

import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.tps.TPSLimitService;
import cn.edu.esf.tps.TPSResult;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/18
 */
public class TPSLimitComponent implements TPSLimitService{
    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public TPSResult process(ESFRequest hsfRequest) {
        return new TPSResult();
    }
}
