package cn.edu.esf.invoke;

import cn.edu.esf.RemotingURL;
import cn.edu.esf.callback.ESFResponseFuture;
import cn.edu.esf.client.Client;
import cn.edu.esf.client.ClientFactory;
import cn.edu.esf.client.NettyClientFactory;
import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.domain.ESFResponse;
import cn.edu.esf.exception.ESFException;
import cn.edu.esf.model.matedata.ServiceMetadata;
import cn.edu.esf.remoting.InvokeService;

import java.util.concurrent.Future;

/**
 * @Author heyong
 * @Date 2017/1/8
 */
public class SyncInvokeComponent implements InvokeService {

    @Override
    public String getKey() {
        return INVOKE_REMOTING_SYNC;
    }

    @Override
    public Object invoke(ESFRequest request, ServiceMetadata metadata, RemotingURL targetURL, byte codecType, int timeout) throws ESFException {
        try {
            Client client = NettyClientFactory.getInstance().getClient(targetURL);
            Future<Object> future = client.futureInvoke(request,codecType,timeout);
            ESFResponseFuture.setFuture(future);

            return ESFResponseFuture.getResponse(timeout);
        } catch (ESFException e) {
            throw e;
        }

    }
}
