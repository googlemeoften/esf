package cn.edu.esf.client;

import cn.edu.esf.*;
import cn.edu.esf.async.ChannelFutureWrapper;
import cn.edu.esf.async.ResponseCallBackFuture;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @Author heyong
 * @Date 2016/12/14
 */
public class NettyClient extends AbstractClient {

    private final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    private final Channel channel;
    private final String localAddress;
    private final String remoteAddress;

    public NettyClient(RemotingURL remotingURL, int timeout, Channel channel) {
        super(remotingURL, timeout);
        this.channel = channel;
        this.localAddress = ((InetSocketAddress) (this.channel.localAddress())).toString();
        this.remoteAddress = ((InetSocketAddress) (this.channel.remoteAddress())).toString();
    }

    @Override
    public ChannelFutureWrapper sendRequest(final BaseRequest request, final int timeout) throws Exception {

        final long begin = System.currentTimeMillis();
        ChannelFuture future = channel.writeAndFlush(request);

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("sucess");
                    return;
                }

                String errorMsg = null;

                if ((System.currentTimeMillis() - begin) > timeout) {
                    errorMsg = "send to server spend too long time(" + (System.currentTimeMillis() - begin)
                            + "),request id :" + request.getRequestID();
                }

                if (future.isCancelled()) {
                    errorMsg = "request be cancelled by user,request id :" + request.getRequestID();
                }

                if (!future.isSuccess()) {
                    NettyClientFactory.getInstance().remove(channel);
                    errorMsg = "send request error:" + future.cause();
                }

                LOGGER.error(errorMsg);
                BaseResponse response = request.createErrorResponse(errorMsg);
                response.setStatus(ResponseStatus.CLIENT_ERROR);
                NettyClient.this.putResponse(response);
            }
        });

        return new NettyChannelFutureWrapper(future);
    }

    @Override
    public void startTiming(final BaseRequest request, int timeout) {
        NettyWorkerThread.timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                ResponseCallBackFuture future = NettyClient.this.removeCallback(request.getRequestID());
                if (future != null) {
                    String errorInfo = "time out";
                    BaseResponse response = request.createErrorResponse(errorInfo);
                    response.setStatus(ResponseStatus.CLIENT_TIMEOUT);
                    future.onResponse(response);
                }
            }
        }, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void doClose(String cause) {

    }


    @Override
    public ClientFactory getClientFactory() {
        return null;
    }

    @Override
    public boolean isConnected() {
        return channel.isActive();
    }

    @Override
    public String getLocalAddress() {
        return localAddress;
    }

    @Override
    public String getRemoteAddress() {
        return remoteAddress;
    }
}
