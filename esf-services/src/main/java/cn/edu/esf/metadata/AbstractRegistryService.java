package cn.edu.esf.metadata;

import cn.edu.esf.thread.NamedThreadFactory;
import cn.edu.esf.thread.ThreadNameSpace;
import cn.edu.esf.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.edu.esf.metadata.NotifyListener.NotifyEvent.CHILD_ADDED;
import static cn.edu.esf.metadata.NotifyListener.NotifyEvent.CHILD_REMOVED;
import static cn.edu.esf.metadata.RegisterMeta.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Created by HeYong on 2017/4/5.
 */
public abstract class AbstractRegistryService implements RegistryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRegistryService.class);
    //缓存注册信息
    private final LinkedBlockingQueue<RegisterMeta> registerMetas = new LinkedBlockingQueue(1024);
    //注册中心链接线程
    private final ExecutorService configExecuter = Executors.newCachedThreadPool(new NamedThreadFactory(ThreadNameSpace.ESF_CONFIG));
    private final ReentrantReadWriteLock registriesLock = new ReentrantReadWriteLock();
    private final ConcurrentMap<ServiceMeta, CopyOnWriteArrayList<NotifyListener>> subscribeListeners = new ConcurrentHashMap<>();
    // Consumer已订阅的信息
    private final ConcurrentSkipListSet<ServiceMeta> subscribeSet = new ConcurrentSkipListSet<>();
    // Provider已发布的注册信息
    private final ConcurrentSkipListSet<RegisterMeta> registerMetaSet = new ConcurrentSkipListSet<>();

    private final Map<ServiceMeta, Pair<Long, List<RegisterMeta>>> registries = new HashMap<>();

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    public AbstractRegistryService() {
        configExecuter.execute(new Runnable() {
            @Override
            public void run() {
                while (!shutdown.get()) {
                    RegisterMeta meta = null;

                    try {
                        meta = registerMetas.take();
                        doRegister(meta);
                    } catch (Throwable throwable) {
                        if (meta != null) {
                            LOGGER.warn("Register [{}] fail: {}, will try again...", meta.getServiceMeta(), throwable.getCause());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void publish(RegisterMeta meta) {
        registerMetas.add(meta);
    }

    @Override
    public void unpublish(RegisterMeta meta) {
        doUnregister(meta);
    }

    @Override
    public void subscribe(ServiceMeta serviceMeta, NotifyListener listener) {
        CopyOnWriteArrayList<NotifyListener> listeners = subscribeListeners.get(serviceMeta);
        if (listeners == null) {
            CopyOnWriteArrayList<NotifyListener> newListeners = new CopyOnWriteArrayList<>();
            listeners = subscribeListeners.putIfAbsent(serviceMeta, newListeners);
            if (listeners == null) {
                listeners = newListeners;
            }
        }
        listeners.add(listener);

        doSubscribe(serviceMeta);
    }

    @Override
    public Collection<RegisterMeta> lookup(ServiceMeta serviceMeta) {
        Pair<Long, List<RegisterMeta>> data;

        final Lock readLock = registriesLock.readLock();
        readLock.lock();
        try {
            data = registries.get(serviceMeta);
        } finally {
            readLock.unlock();
        }

        if (data != null) {
            return new ArrayList<>(data.getValue());
        }
        return Collections.emptyList();
    }

    // 通知新增或删除服务
    protected void notify(ServiceMeta serviceMeta, RegisterMeta registerMeta, NotifyListener.NotifyEvent event, long version) {
        boolean notifyNeeded = false;

        final Lock writeLock = registriesLock.writeLock();
        writeLock.lock();
        try {
            Pair<Long, List<RegisterMeta>> data = registries.get(serviceMeta);
            if (data == null) {
                if (event == CHILD_REMOVED) {
                    return;
                }
                List<RegisterMeta> metaList = new ArrayList<RegisterMeta>();
                metaList.add(registerMeta);
                data = new Pair<>(version, metaList);
                notifyNeeded = true;
            } else {
                long oldVersion = data.getKey();
                List<RegisterMeta> metaList = data.getValue();
                if (oldVersion < version || (version < 0 && oldVersion > 0 /* version 溢出 */)) {
                    if (event == CHILD_REMOVED) {
                        metaList.remove(registerMeta);
                    } else if (event == CHILD_ADDED) {
                        metaList.add(registerMeta);
                    }
                    data = new Pair<>(version, metaList);
                    notifyNeeded = true;
                }
            }

            registries.put(serviceMeta, data);
        } finally {
            writeLock.unlock();
        }

        if (notifyNeeded) {
            CopyOnWriteArrayList<NotifyListener> listeners = subscribeListeners.get(serviceMeta);
            if (listeners != null) {
                for (NotifyListener l : listeners) {
                    l.notify(registerMeta, event);
                }
            }
        }
    }

    protected abstract void doSubscribe(ServiceMeta serviceMeta);

    protected abstract void doRegister(RegisterMeta meta);

    protected abstract void doUnregister(RegisterMeta meta);

    public ConcurrentSkipListSet<ServiceMeta> subscribeSet() {
        return subscribeSet;
    }

    public ConcurrentSkipListSet<RegisterMeta> registerMetaSet() {
        return registerMetaSet;
    }

    public Map<ServiceMeta, Pair<Long, List<RegisterMeta>>> getRegistries() {
        return registries;
    }
}
