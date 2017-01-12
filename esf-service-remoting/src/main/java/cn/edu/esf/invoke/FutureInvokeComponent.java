package cn.edu.esf.invoke;

import cn.edu.esf.RemotingURL;
import cn.edu.esf.client.Client;
import cn.edu.esf.client.ClientFactory;
import cn.edu.esf.client.NettyClientFactory;
import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.domain.ESFResponse;
import cn.edu.esf.exception.ESFException;
import cn.edu.esf.model.ServiceMetadata;
import cn.edu.esf.remoting.InvokeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author heyong
 * @Date 2017/1/8
 */
public class FutureInvokeComponent implements InvokeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FutureInvokeComponent.class);
    private final ClientFactory clientFactory = NettyClientFactory.getInstance();


    @Override
    public Object invoke(ESFRequest request, ServiceMetadata metadata, RemotingURL targetURL, byte codecType, int timeout) throws ESFException {
        ESFResponse response = null;
        try {
            Client client = clientFactory.getClient(targetURL);
            Future<Object> future = client.futureInvoke(request, codecType, timeout);
            response = (ESFResponse) future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        return response.getResponseObject();
    }
}
