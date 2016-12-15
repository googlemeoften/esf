package cn.edu.esf.client;


public class ClientTest {

    public static void main(String[] args) throws Exception {
        NettyClientFactory factory = NettyClientFactory.getInstance();
        NettyClient client = (NettyClient) factory.createClient("127.0.0.1");
        client.sendRequest("hello", 4000);

       // Thread.sleep(100000);
    }

}
