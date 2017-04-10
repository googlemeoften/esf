package cn.edu.esf.invoke;

import cn.edu.esf.RemotingURL;
import cn.edu.esf.callback.ESFResponseCallback;
import cn.edu.esf.client.Client;
import cn.edu.esf.client.NettyClientFactory;
import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.exception.ESFException;
import cn.edu.esf.model.matedata.ServiceMetadata;
import cn.edu.esf.model.matedata.ServiceMetadata.AsyncallMethod;
import cn.edu.esf.remoting.InvokeService;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/19
 */
public class CallbackInvokeComponent implements InvokeService {

    private static final String INVOKE_REMOTE_CALLBACK = "CALLBACK";

    @Override
    public String getKey() {
        return INVOKE_REMOTE_CALLBACK;
    }

    @Override
    public Object invoke(ESFRequest request, ServiceMetadata metadata, RemotingURL targetURL, byte codecType, int timeout) throws ESFException {
        final String methodName = request.getMethodName();
        AsyncallMethod asyncMethod = metadata.getAsyncallMethod(methodName);

        Client client = NettyClientFactory.getInstance().createClient(targetURL);
        ESFResponseCallback callback = (ESFResponseCallback) asyncMethod.getCallbackInstance();

        return null;
    }
}
