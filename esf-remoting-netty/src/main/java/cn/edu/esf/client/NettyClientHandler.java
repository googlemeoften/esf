package cn.edu.esf.client;

import cn.edu.esf.BaseResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author heyong
 * @Date 2016/12/14
 */
public class NettyClientHandler extends IdleStateHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientHandler.class);
    private final NettyClientFactory factory;

    public NettyClientHandler(NettyClientFactory factory) {
        super(27, 0, 0);
        this.factory = factory;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof BaseResponse) {
            long requestID = ((BaseResponse) msg).getRequestID();
            Object response = ((BaseResponse) msg).getResponseObject(null);

            System.out.println(requestID + "-" + response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client has connected with server...");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {

    }


}
