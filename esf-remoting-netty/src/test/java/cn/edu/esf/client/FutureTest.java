package cn.edu.esf.client;

import cn.edu.esf.BaseResponse;
import cn.edu.esf.HelloService;
import cn.edu.esf.RemotingURL;
import cn.edu.esf.async.ResponseCallBackFuture;
import cn.edu.esf.domain.ESFRequest;

import java.lang.reflect.Method;

/**
 * @Author heyong
 * @Date 2017/1/4
 */
public class FutureTest {
    public static void main(String[] args) throws Exception {
        NettyClientFactory factory = NettyClientFactory.getInstance();

        RemotingURL url = new RemotingURL("127.0.0.1", "ESF", "127.0.0.1", 8080, null, null);

        NettyClient client = (NettyClient) factory.createClient(url);

        Class clazz = HelloService.class;
        String targetInstance = clazz.getName();
        Method method = clazz.getMethod("sayHello", String.class);
        String methodName = method.getName();
        Class<?>[] methodTypes = method.getParameterTypes();
        String[] argTypes = new String[methodTypes.length];
        int i = 0;
        for (Class arg : methodTypes) {
            argTypes[i++] = arg.getName();
        }

        String[] methodArg = {"hello"};

        ESFRequest request = new ESFRequest();
        request.setServiceName(targetInstance);
        request.setMethodName(methodName);
        request.setMethodArgTypes(argTypes);
        request.setMethosArgs(methodArg);

//        Object response = client.syncInvoke(request, (byte) 1, 4000);
        ResponseCallBackFuture future = (ResponseCallBackFuture) client.futureInvoke(request, (byte) 1, 4000);
        Thread.sleep(4000);
        BaseResponse response = (BaseResponse) future.get();

        long requestID = ((BaseResponse) response).getRequestID();
        Object result = ((BaseResponse) response).getResponseObject(null);

        System.out.println(requestID + "-" + result);
    }
}
