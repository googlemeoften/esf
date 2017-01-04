package cn.edu.esf.client;

import cn.edu.esf.BaseRequest;
import cn.edu.esf.BaseResponse;
import cn.edu.esf.HeartBeatRequest;
import cn.edu.esf.ResponseStatus;
import cn.edu.esf.async.ResponseCallBackFuture;
import cn.edu.esf.async.SendCallBackListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executor;

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
       /*   该段代码用于测试netty通信是否成功
        if (msg instanceof BaseResponse) {
            long requestID = ((BaseResponse) msg).getRequestID();
            Object response = ((BaseResponse) msg).getResponseObject(null);

            System.out.println(requestID + "-" + response);
        }*/

        if (msg instanceof BaseResponse) {
            BaseResponse response = (BaseResponse) msg;
            factory.getClientByChannel(ctx.channel()).putResponse(response);
        } else {
            LOGGER.error("receive message error,only support BaseResponse");
            throw new Exception("receive message error,only support BaseResponse");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.warn(cause.getMessage());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client has connected with server...");
        //以后这里可能要做监控，监控连接数
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //以后这里可能要做监控，监控连接数
        factory.remove(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        final Client client = factory.getClientByChannel(ctx.channel());
        BaseRequest heartbeatRequest = new HeartBeatRequest();

        client.invokeWithCallBack(heartbeatRequest, new HeartBeatListener(client));
    }


    private final class HeartBeatListener implements SendCallBackListener {
        private final Client client;
        private static final String HEARBEAT_FAIL_COUNT = "HEARBEAT_FAIL_COUNT";

        public HeartBeatListener(Client client) {
            this.client = client;
        }

        @Override
        public void onResponse(BaseResponse response) {
            if (response == null || response.getStatus() != ResponseStatus.OK) {
                closeIfPossible();
            } else {
                client.getAttributes().remove(HEARBEAT_FAIL_COUNT);
                LOGGER.debug("心跳检测成功：" + client.getRemoteAddress());
            }
        }

        /**
         * 如果
         */
        private void closeIfPossible() {
            Integer count = (Integer) client.getAttributes().putIfAbsent(HEARBEAT_FAIL_COUNT, 1);

            if (count != null) {
                count++;
                if (count < 2) {
                    client.getAttributes().put(HEARBEAT_FAIL_COUNT, count);
                } else {
                    LOGGER.warn("心跳检测失败，断开连接：" + client.getRemoteAddress());
                    client.close("heartbeat超过两次次失败");
                }
            }
        }

        @Override
        public Executor getExecutor() {
            return null;
        }
    }
}



