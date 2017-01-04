package cn.edu.esf;

import cn.edu.esf.thread.NamedThreadFactory;
import cn.edu.esf.thread.ThreadNameSpace;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import io.netty.util.internal.SystemPropertyUtil;

/**
 * @Author heyong
 * @Date 2016/12/14
 */
public class NettyWorkerThread {
    public static final NioEventLoopGroup workerGroup = new NioEventLoopGroup(SystemPropertyUtil.getInt("io.workers",
            Runtime.getRuntime().availableProcessors() * 2), new NamedThreadFactory(ThreadNameSpace.ESF_NETTY_WORKER));

    public static final Timer timer = new HashedWheelTimer(new NamedThreadFactory(ThreadNameSpace.ESF_REMOTING_TIMER));

    public static final ByteBufAllocator byteBufAllocator;

    static {
        workerGroup.setIoRatio(SystemPropertyUtil.getInt("esf.ioratio", 100));

        if (SystemPropertyUtil.getBoolean("esf.bytebuf.pool", false)) {
            byteBufAllocator = PooledByteBufAllocator.DEFAULT;
        } else {
            byteBufAllocator = UnpooledByteBufAllocator.DEFAULT;
        }
    }


}
