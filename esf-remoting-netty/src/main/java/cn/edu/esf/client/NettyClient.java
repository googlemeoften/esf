package cn.edu.esf.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author heyong
 * @Date 2016/12/14
 */
public class NettyClient extends AbstractClient {

    private final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    private final Channel channel;

    public NettyClient(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void sendRequest(String str, int timeout) throws Exception {
        ChannelFuture future = channel.writeAndFlush(str);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("sucess");
                    return;
                }
            }
        });
    }
}
