package cn.edu.esf.client;


import cn.edu.esf.HelloService;
import cn.edu.esf.RemotingConstants;
import cn.edu.esf.RemotingURL;
import cn.edu.esf.RpcRequest;


public class ClientTest {

    public static void main(String[] args) throws Exception {
        NettyClientFactory factory = NettyClientFactory.getInstance();

        RemotingURL url = new RemotingURL("127.0.0.1", "ESF", "127.0.0.1", 8080, null, null);

        NettyClient client = (NettyClient) factory.createClient(url);

        Class clazz = HelloService.class;
        String targetInstance = clazz.getName();
        String methodName = clazz.getMethod("sayHello", String.class).getName();
        String[] argsTypes = {String.class.getName()};
        byte[][] requestObject = new byte[1][];
        byte[] bytes = "hello".getBytes(RemotingConstants.DEFAULT_CHARSET);
        requestObject[0] = bytes;

        RpcRequest request = new RpcRequest(100L, 2000, targetInstance, methodName, argsTypes, requestObject, "hello".getBytes(), (byte)1);
        client.sendRequest(request, 2000);

    }

}
