package cn.edu.esf.server;

import cn.edu.esf.NettyWorkerThread;
import cn.edu.esf.encoder.NettyProtocolDecoder;
import cn.edu.esf.encoder.NettyProtocolEncoder;
import cn.edu.esf.protocol.ProtocolFactory;
import cn.edu.esf.thread.NamedThreadFactory;
import cn.edu.esf.thread.ThreadNameSpace;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author heyong
 * @Date 2016/12/14
 */
public class NettyServer implements Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);
    private final EventLoopGroup boss = new NioEventLoopGroup(0, new NamedThreadFactory(ThreadNameSpace.ESF_NETTY_BOSS));
    private final EventLoopGroup worker = NettyWorkerThread.workerGroup;
    private final NettyServerHandler serverHandler = new NettyServerHandler();
    private final AtomicBoolean startFlag = new AtomicBoolean(false);
    private final String bindHost;

    public NettyServer(final RpcRequestProcessor rpcProcessor, final String bindHost) {
        this.bindHost = bindHost;
        ProtocolFactory.instance.initServerSide(rpcProcessor);
    }

    @Override
    public void start(int listenPort) throws Exception {
        if (!startFlag.compareAndSet(false, true)) {
            return;
        }
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, NettyWorkerThread.byteBufAllocator)
                .childOption(ChannelOption.ALLOCATOR, NettyWorkerThread.byteBufAllocator)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("decoder", new NettyProtocolDecoder())
                                .addLast("encoder", new NettyProtocolEncoder())
                                .addLast("handler", serverHandler);
                    }
                });

        int tryBind = 3;
        while (tryBind > 0) {
            ChannelFuture future = bootstrap.bind(new InetSocketAddress(bindHost, listenPort));
            future.await();
            if (future.isSuccess()) {
//                System.out.println("Server is start,listen at: " + listenPort);
                LOGGER.info("Server is start,listen at: " + listenPort);
                return;
            } else {
                tryBind--;
                if (tryBind <= 0) {
                    LOGGER.warn("After 3 failed attempts to start server at port : " + listenPort
                            + ", we are shutting down the vm");
                } else {
                    LOGGER.warn("Failed to start server at port : " + listenPort + ", Sleep 3s and try again",
                            future.cause());
                    Thread.sleep(3000);
                }
            }
        }
    }

    @Override
    public void stop() {
        LOGGER.warn("Server stop....");
        this.startFlag.set(false);
        this.boss.shutdownGracefully();
        this.worker.shutdownGracefully();
    }
}
