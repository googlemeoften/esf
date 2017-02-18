package cn.edu.esf.server;

import cn.edu.esf.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * Netty Server Handler
 * @Author heyong
 * @Date 2016/12/14
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<BaseRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);
    private final ConcurrentHashMap<Channel, NettyConnection> channels = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseRequest request) throws Exception {
        handleRequest(ctx, request);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client:" + ((InetSocketAddress) ctx.channel().remoteAddress()).getHostName() + "connect to server");
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

    private void handleRequest(ChannelHandlerContext ctx, BaseRequest request) {
        NettyConnection connection = channels.get(ctx.channel());
        connection.refreshLastTime(System.currentTimeMillis());

        ProtocolHandler handler = request.getProcotolHandler();
        Executor executor = handler.getExecutor(request);

        if(executor == null){
            handler.handleRequest(request,connection,System.currentTimeMillis());
        }else {
            executor.execute(new HandlerRunnable(connection,request,handler));
        }
    }

    private static class HandlerRunnable implements Runnable {
        private final Connection connection;
        private final BaseRequest message;
        private final ProtocolHandler<BaseRequest> serverHandler;
        private final long dispatchTime = System.currentTimeMillis();

        public HandlerRunnable(Connection conneciton, BaseRequest message, ProtocolHandler<BaseRequest> serverHandler) {
            this.connection = conneciton;
            this.message = message;
            this.serverHandler = serverHandler;
        }

        public void run() {
            serverHandler.handleRequest(message, connection, dispatchTime);
        }

    }
}

