package cn.edu.esf.client;

import cn.edu.esf.BaseResponse;
import cn.edu.esf.HelloService;
import cn.edu.esf.HelloServiceImpl;
import cn.edu.esf.RemotingURL;
import cn.edu.esf.async.ResponseCallBackFuture;
import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.domain.ESFResponse;
import cn.edu.esf.exception.ESFException;

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

        //for (int i = 0; i < 10; i++) {
        Thread thread = new Thread(new Task(client));
        thread.start();
        //}
    }
}

class Task implements Runnable {
    private Client client;

    public Task(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        Class clazz = HelloServiceImpl.class;
        String targetInstance = clazz.getName();
        Method method = null;
        try {
            method = clazz.getMethod("sayHello", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        String methodName = method.getName();
        Class<?>[] methodTypes = method.getParameterTypes();
        String[] argTypes = new String[methodTypes.length];
        int i = 0;
        for (Class arg : methodTypes) {
            argTypes[i++] = arg.getName();
        }

        String[] methodArg = {"world"};

        ESFRequest request = new ESFRequest();
        request.setServiceName(targetInstance);
        request.setMethodName(methodName);
        request.setMethodArgTypes(argTypes);
        request.setMethosArgs(methodArg);

        BaseResponse response = null;
        while (true) {
            try {
                response = client.syncInvoke(request, (byte) 1, 10000);
            } catch (ESFException e) {
                e.printStackTrace();
            }
//        ResponseCallBackFuture future = null;
//        try {
//            future = (ResponseCallBackFuture) client.futureInvoke(request, (byte) 1, 4000);
//        } catch (ESFException e) {
//            e.printStackTrace();
//        }
//        BaseResponse response = (BaseResponse) future.get();

            long requestID = response.getRequestID();
            ESFResponse result = (ESFResponse) response.getResponseObject(null);

            System.out.println(requestID + "-" + result.getResponseObject());
        }
    }
}
