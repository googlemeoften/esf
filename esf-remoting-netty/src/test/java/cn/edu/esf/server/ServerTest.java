package cn.edu.esf.server;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public class ServerTest {
    public static void main(String[] args) throws Exception {
        NettyServer server = new NettyServer("127.0.0.1");
        server.start(8080);
        //Thread.sleep(1000000);
    }
}
