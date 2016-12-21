package cn.edu.esf.pool;

import cn.edu.esf.thread.NamedThreadFactory;
import cn.edu.esf.thread.ThreadNameSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.*;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public class ThreadPoolManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolManager.class);

    private final long keepAlive = 300L;
    private final ThreadPoolExecutor defaultPoolExecutor;
    private final ConcurrentHashMap<String, ThreadPoolExecutor> executors = new ConcurrentHashMap<>();
    private final RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();

    public ThreadPoolManager(int corePoolSize, int maximumPoolSize, int queueSize) {
        BlockingQueue<Runnable> taskQueue = new SynchronousQueue<>();
        ThreadFactory factory = new NamedThreadFactory(ThreadNameSpace.ESF_PROCESSER);
        defaultPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAlive,
                TimeUnit.SECONDS, taskQueue, factory, handler);
    }

    public void allocThreadPool(String serviceName, int corePoolSize, int maximumPoolSize) throws Exception {
        if (executors.containsKey(serviceName)) {
            throw new Exception("ThreadManager has alloc threadP" +
                    "ool to " + serviceName);
        }

        if (defaultPoolExecutor == null || defaultPoolExecutor.isShutdown()) {
            throw new Exception("ThreadManeger Cann't alloc threadPool to " + serviceName);
        }

        int blance = defaultPoolExecutor.getMaximumPoolSize();

        if (blance < maximumPoolSize) {
            throw new Exception("ThreadManeger failed alloc thread to  " + serviceName
                    + ": blance:" + blance + " maximumPoolSize:" + maximumPoolSize
            );
        }

        ThreadFactory threadFactory = new NamedThreadFactory(ThreadNameSpace.getNettyProcessorService(serviceName));
        try {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAlive, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), threadFactory, handler);
            executors.put(serviceName, executor);
        } catch (Exception e) {
            throw new Exception("ThreadManager alloc failed to " + serviceName);
        }

        int newBlance = blance - maximumPoolSize;
        if (newBlance == 0) {
            defaultPoolExecutor.shutdown();
        } else {

            if (newBlance < defaultPoolExecutor.getCorePoolSize()) {
                defaultPoolExecutor.setCorePoolSize(newBlance);
            } else {
                defaultPoolExecutor.setMaximumPoolSize(newBlance);
            }
        }
    }

    public ThreadPoolExecutor getExecutor(String serviceName) {
        if (!executors.isEmpty()) {
            ThreadPoolExecutor executor = executors.get(serviceName);
            if (executor != null) {
                return executor;
            }
        }
        return defaultPoolExecutor;
    }

    public void shutDown() {
        if (defaultPoolExecutor != null && !defaultPoolExecutor.isShutdown()) {
            defaultPoolExecutor.shutdown();
        }
        if (!executors.isEmpty()) {
            Iterator<ThreadPoolExecutor> iterator = executors.values().iterator();

            while (iterator.hasNext()) {
                iterator.next().shutdown();
            }
        }
    }

}
