package cn.edu.esf.client;

import cn.edu.esf.NettyWorkerThread;
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
    public Client createClient(String url) throws Exception {
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
                        ch.pipeline().addLast("decoder", new StringDecoder())
                                .addLast("encoder", new StringEncoder())
                                .addLast("handler", handler);
                    }
                });

        int connectTimeout = 4000;

         bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);

        /**
         * =====================
         */
        String targetIp = "127.0.0.1";
        int targetPort = 8080;

        ChannelFuture future = bootstrap.connect(new InetSocketAddress(targetIp, targetPort));
        if (future.awaitUninterruptibly(connectTimeout) && future.isSuccess() && future.channel().isActive()) {
            Channel channel = future.channel();
            Client client = new NettyClient(channel);
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
}
