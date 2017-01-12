package cn.edu.esf.invoke;

import cn.edu.esf.RemotingURL;
import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.exception.ESFException;
import cn.edu.esf.model.ServiceMetadata;
import cn.edu.esf.remoting.InvokeService;

/**
 * @Author heyong
 * @Date 2017/1/8
 */
public class SyncInvokeComponent implements InvokeService {

    @Override
    public Object invoke(ESFRequest request, ServiceMetadata metadata, RemotingURL targetURL, byte codecType, int timeout) throws ESFException {
        return null;
    }
}
