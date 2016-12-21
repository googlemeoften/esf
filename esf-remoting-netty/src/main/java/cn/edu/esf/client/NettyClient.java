package cn.edu.esf.client;

import cn.edu.esf.BaseRequest;
import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.domain.ESFResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

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
    public void sendRequest(final BaseRequest request, int timeout) throws Exception {
        ChannelFuture future = channel.writeAndFlush(request);
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

    @Override
    public ESFResponse syncInvoke(ESFRequest request, byte codecType, int timeout) throws Exception {
        return null;
    }

    @Override
    public Future<Object> futureInvoke(ESFRequest request, byte codecType, int timeout) throws Exception {
        return null;
    }
}
