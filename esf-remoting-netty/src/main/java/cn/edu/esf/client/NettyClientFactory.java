package cn.edu.esf.client;

import cn.edu.esf.NettyWorkerThread;
import cn.edu.esf.RemotingURL;
import cn.edu.esf.constant.TRConstants;
import cn.edu.esf.encoder.NettyProtocolDecoder;
import cn.edu.esf.encoder.NettyProtocolEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author heyong
 * @Date 2016/12/14
 */
public class NettyClientFactory extends AbstractClientFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientFactory.class);
    private static NettyClientFactory instance = new NettyClientFactory();

    private final ConcurrentHashMap<Channel, Client> channel2Client = new ConcurrentHashMap<>();

    private NettyClientFactory() {

    }

    public static NettyClientFactory getInstance() {
        return instance;
    }

    @Override
    public Client createClient(RemotingURL url) throws Exception {
        final Bootstrap bootstrap = new Bootstrap();
        final NettyClientHandler handler = new NettyClientHandler(this);
        bootstrap.group(NettyWorkerThread.workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.ALLOCATOR, NettyWorkerThread.byteBufAllocator)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast("decoder", new NettyProtocolDecoder())
                                .addLast("encoder", new NettyProtocolEncoder())
                                .addLast("handler", handler);
                    }
                });

        int connectTimeout = url.getParameter(TRConstants.CONNECT_TIMEOUT_KEY, 4000);
        if (connectTimeout < 1000) {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 4000);
        } else {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        }


        String targetIp = url.getHost();
        int targetPort = url.getPort();

        ChannelFuture future = bootstrap.connect(new InetSocketAddress(targetIp, targetPort));
        if (future.awaitUninterruptibly(connectTimeout) && future.isSuccess() && future.channel().isActive()) {
            Channel channel = future.channel();
            Client client = new NettyClient(url, connectTimeout, channel);
            channel2Client.put(channel, client);
            return client;
        } else {
            future.cancel(true);
            future.channel().close();
            LOGGER.warn("[remoting] failure to connect:" + targetIp);
            //throw new RemotingUncheckedException(113, targetIP, targetPort + "", connectTimeout + "");
            throw new Exception();
        }
    }

    public Client getClientByChannel(Channel channel) {
        return channel2Client.get(channel);
    }

    public void remove(final Channel channel) {
        Client client = channel2Client.remove(channel);
        if (client != null) {
            client.removeAllRequestCallBackWhenClose();

        }
    }
}
