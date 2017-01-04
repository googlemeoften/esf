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

/**
 * @Author heyong
 * @Date 2016/12/14
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<BaseRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);
    private final ConcurrentHashMap<Channel, NettyConnection> channels = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseRequest request) throws Exception {
        System.out.println("server get info....");
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

        System.out.println(request);

        if (request instanceof RpcRequest) {
            //===================
            RpcRequest msg = (RpcRequest) request;
            long requestID = request.getRequestID();
            String targetInstance = msg.getTargetInstance();
            String methodName = msg.getMethodName();

            ResponseStatus status = ResponseStatus.OK;
            byte[] bytes = "hello".getBytes();

            System.out.println(MessageFormat.format("requestID: {0},targetInstance:{1},method:{2}", requestID, targetInstance, methodName));
            RpcResponse response = new RpcResponse(requestID, (byte) 1, status, bytes);

            //=======================
            connection.writeResponseToChannel(response);
        } else {
            HeartBeatRequest msg = (HeartBeatRequest) request;
            HeartBeatResponse response = new HeartBeatResponse(msg.getRequestID());
            connection.writeResponseToChannel(response);
        }
    }
}
