package cn.edu.esf.client;


import cn.edu.esf.HelloService;
import cn.edu.esf.RemotingConstants;
import cn.edu.esf.RpcRequest;


public class ClientTest {

    public static void main(String[] args) throws Exception {
        NettyClientFactory factory = NettyClientFactory.getInstance();
        NettyClient client = (NettyClient) factory.createClient("127.0.0.1");

        Class clazz = HelloService.class;
        String targetInstance = clazz.getName();
        String methodName = clazz.getMethod("sayHello", String.class).getName();
        String[] argsTypes = {String.class.getName()};
        byte[][] requestObject = new byte[1][];
        byte[] bytes = "hello".getBytes(RemotingConstants.DEFAULT_CHARSET);
        requestObject[0] = bytes;

        RpcRequest request = new RpcRequest(100L, 2000, targetInstance, methodName, argsTypes, requestObject, "hello".getBytes(), 0);
        client.sendRequest(request, 2000);

    }

}
