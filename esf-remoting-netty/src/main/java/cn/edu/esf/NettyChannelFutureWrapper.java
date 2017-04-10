package cn.edu.esf;

import cn.edu.esf.async.ChannelFutureWrapper;
import io.netty.channel.ChannelFuture;

/**
 * @Author heyong
 * @Date 2017/1/3
 */
public class NettyChannelFutureWrapper implements ChannelFutureWrapper {
    private final ChannelFuture channelFuture;

    public NettyChannelFutureWrapper(final ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

    @Override
    public boolean cancel() {
        return channelFuture.isCancelled();
    }

}