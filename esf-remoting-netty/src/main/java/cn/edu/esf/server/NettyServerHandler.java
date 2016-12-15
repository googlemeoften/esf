package cn.edu.esf.server;

import cn.edu.esf.Connection;
import cn.edu.esf.NettyConnection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author heyong
 * @Date 2016/12/14
 */
public class NettyServerHandler extends SimpleChannelInboundHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);
    private final ConcurrentHashMap<Channel, NettyConnection> channels = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        handleRequest(ctx, msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        channels.putIfAbsent(channel, new NettyConnection(channel));
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        channel.close();
        channels.remove(channel);
        super.channelInactive(ctx);
    }

    private void handleRequest(ChannelHandlerContext ctx, Object msg) {
        NettyConnection connection = channels.get(ctx.channel());
        connection.refreshLastTime(System.currentTimeMillis());

        System.out.println(msg);
        connection.writeResponseToChannel("world");
    }
}
