package cn.edu.esf.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂，为每个线程命名
 *
 * @Author heyong
 * @Date 2016/12/14
 */
public class NamedThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNum = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;
    private final AtomicInteger threadNum = new AtomicInteger(1);
    public final boolean isDaemon;

    public NamedThreadFactory(String namePrefix) {
        this(namePrefix, true);
    }

    public NamedThreadFactory(String namePrefix, boolean isDaemon) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix + "-" + poolNum.getAndIncrement() + "-thread-";
        this.isDaemon = true;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNum.getAndIncrement(), 0);
        t.setContextClassLoader(this.getClass().getClassLoader());
        t.setPriority(Thread.NORM_PRIORITY);
       // t.setDaemon(isDaemon);
        return t;
    }
}
