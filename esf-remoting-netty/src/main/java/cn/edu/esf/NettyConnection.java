package cn.edu.esf;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public class NettyConnection implements Connection {
    private Logger LOGGER = LoggerFactory.getLogger(NettyConnection.class);
    private final Channel channel;
    private final String remotingIp;

    private volatile long lastReadTime = System.currentTimeMillis();

    public NettyConnection(Channel channel) {
        this.channel = channel;
        this.remotingIp = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
    }

    @Override
    public SocketAddress getLocalAddress() {
        return channel.localAddress();
    }

    @Override
    public SocketAddress getRemotingAddress() {
        return channel.remoteAddress();
    }

    @Override
    public void writeResponseToChannel(final BaseResponse response) {
        if (response != null) {
            ChannelFuture future = channel.writeAndFlush(response);
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        LOGGER.warn("Server write Response error ,request id  is :" + response
                                + ",channel: " + remotingIp + future.cause());
                        if (!channel.isActive()) {
                            channel.close();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void refreshLastTime(long lastTime) {
        this.lastReadTime = lastTime;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }
}
