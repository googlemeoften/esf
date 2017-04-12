package cn.edu.esf.metadata.component;

import cn.edu.esf.metadata.AbstractRegistryService;
import cn.edu.esf.metadata.RegisterMeta;
import cn.edu.esf.utils.NetUtil;
import cn.edu.esf.utils.Pair;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.jboss.netty.util.internal.SystemPropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.edu.esf.metadata.NotifyListener.NotifyEvent.CHILD_ADDED;
import static cn.edu.esf.metadata.NotifyListener.NotifyEvent.CHILD_REMOVED;
import static cn.edu.esf.metadata.RegisterMeta.*;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by HeYong on 2017/4/5.
 */
public class ZookeeperRegistryService extends AbstractRegistryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperRegistryService.class);

    // 没有实际意义, 不要在意它
    private static final AtomicLong sequence = new AtomicLong(0);
    private final String address = SystemPropertyUtil.get("jupiter.local.address", NetUtil.getLocalAddress());
    private final int sessionTimeoutMs = SystemPropertyUtil.getInt("jupiter.registry.zookeeper.sessionTimeoutMs", 60 * 1000);
    private final int connectionTimeoutMs = SystemPropertyUtil.getInt("jupiter.registry.zookeeper.connectionTimeoutMs", 15 * 1000);
    private final ConcurrentMap<RegisterMeta.ServiceMeta, PathChildrenCache> pathChildrenCaches = new ConcurrentHashMap<>();
    // 指定节点都提供了哪些服务
    private final ConcurrentMap<Address, ConcurrentSkipListSet<ServiceMeta>> serviceMetaMap = new ConcurrentHashMap<>();

    private CuratorFramework configClient;

    @Override
    public Collection<RegisterMeta> lookup(ServiceMeta serviceMeta) {
        String directory = String.format("/esf/provider/%s/%s/%s",
                serviceMeta.getGroup(),
                serviceMeta.getVersion(),
                serviceMeta.getServiceName());

        List<RegisterMeta> registerMetas = new ArrayList<>();
        try {

            List<String> paths = configClient.getChildren().forPath(directory);
            for (String path : paths) {
                registerMetas.add(parseRegisterMeta(path));
            }

        } catch (Throwable e) {
            LOGGER.warn("Lookup service meta: {} path failed, {}.", serviceMeta, e.getStackTrace());
        }

        return registerMetas;
    }

    @Override
    public void connectToRegistryServer(String connectString) {
        checkNotNull(connectString, "connectString");
        configClient = CuratorFrameworkFactory.newClient(
                connectString, sessionTimeoutMs, connectionTimeoutMs, new ExponentialBackoffRetry(500, 20));

        configClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {

                LOGGER.info("Zookeeper connection state changed {}.", newState);

                if (newState == ConnectionState.RECONNECTED) {

                    LOGGER.info("Zookeeper connection has been re-established, will re-subscribe and re-register.");

                    // 重新订阅
                    for (ServiceMeta serviceMeta : subscribeSet()) {
                        doSubscribe(serviceMeta);
                    }

                    // 重新发布服务
                    for (RegisterMeta meta : registerMetaSet()) {
                        doRegister(meta);
                    }
                }
            }
        });

        configClient.start();
    }

    @Override
    protected void doSubscribe(RegisterMeta.ServiceMeta serviceMeta) {
        PathChildrenCache childrenCache = pathChildrenCaches.get(serviceMeta);
        if (childrenCache == null) {
            String directory = String.format("/jupiter/provider/%s/%s/%s",
                    serviceMeta.getGroup(),
                    serviceMeta.getVersion(),
                    serviceMeta.getServiceName());

            PathChildrenCache newChildrenCache = new PathChildrenCache(configClient, directory, false);
            childrenCache = pathChildrenCaches.putIfAbsent(serviceMeta, newChildrenCache);
            if (childrenCache == null) {
                childrenCache = newChildrenCache;

                childrenCache.getListenable().addListener(new PathChildrenCacheListener() {

                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {

                        LOGGER.info("Child event: {}", event);

                        switch (event.getType()) {
                            case CHILD_ADDED: {
                                RegisterMeta registerMeta = parseRegisterMeta(event.getData().getPath());
                                Address address = registerMeta.getAddress();
                                ServiceMeta serviceMeta = registerMeta.getServiceMeta();
                                ConcurrentSkipListSet<ServiceMeta> serviceMetaSet = getServiceMeta(address);

                                serviceMetaSet.add(serviceMeta);
                                ZookeeperRegistryService.this.notify(
                                        serviceMeta, registerMeta, CHILD_ADDED, sequence.getAndIncrement());

                                break;
                            }
                            case CHILD_REMOVED: {
                                RegisterMeta registerMeta = parseRegisterMeta(event.getData().getPath());
                                Address address = registerMeta.getAddress();
                                ServiceMeta serviceMeta = registerMeta.getServiceMeta();
                                ConcurrentSkipListSet<ServiceMeta> serviceMetaSet = getServiceMeta(address);

                                serviceMetaSet.remove(serviceMeta);
                                ZookeeperRegistryService.this.notify(
                                        serviceMeta, registerMeta, CHILD_REMOVED, sequence.getAndIncrement());

                                if (serviceMetaSet.isEmpty()) {
                                    LOGGER.info("Offline notify: {}.", address);

                                    // ZookeeperRegistryService.this.offline(address);
                                }
                                break;
                            }
                        }
                    }
                });

                try {
                    childrenCache.start();
                } catch (Exception e) {
                    LOGGER.warn("Subscribe {} failed, {}.", directory, e.getCause());
                }
            }
        }
    }

    @Override
    protected void doRegister(final RegisterMeta meta) {
        String directory = String.format("/jupiter/provider/%s/%s/%s",
                meta.getGroup(),
                meta.getVersion(),
                meta.getServiceProviderName());

        try {
            if (configClient.checkExists().forPath(directory) == null) {
                configClient.create().creatingParentsIfNeeded().forPath(directory);
            }
        } catch (Exception e) {
            LOGGER.warn("Create parent path failed, directory: {}, {}.", directory, e.getCause());
        }

        try {
            meta.setHost(address);
            configClient.create().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
                @Override
                public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                    registerMetaSet().add(meta);
                    LOGGER.info("Register: {}.", meta);
                }
            }).forPath(String.format("%s/%s:%s:%s:%s",
                    directory,
                    meta.getHost(),
                    String.valueOf(meta.getPort()),
                    String.valueOf(meta.getWeight()),
                    String.valueOf(meta.getConnCount())));

        } catch (Exception e) {
            LOGGER.warn("Create register meta: {} path failed, {}.", meta, e.getCause());
        }

    }

    @Override
    protected void doUnregister(final RegisterMeta meta) {
        String directory = String.format("/jupiter/provider/%s/%s/%s",
                meta.getGroup(),
                meta.getVersion(),
                meta.getServiceProviderName());

        try {
            if (configClient.checkExists().forPath(directory) == null) {
                return;
            }
        } catch (Exception e) {
            LOGGER.warn("Check exists with parent path failed, directory: {}, {}.", directory, e.getCause());
        }

        try {
            meta.setHost(address);

            configClient.delete().inBackground(new BackgroundCallback() {

                @Override
                public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                    registerMetaSet().remove(meta);

                    LOGGER.info("Unregister: {}.", meta);
                }
            }).forPath(
                    String.format("%s/%s:%s:%s:%s",
                            directory,
                            meta.getHost(),
                            String.valueOf(meta.getPort()),
                            String.valueOf(meta.getWeight()),
                            String.valueOf(meta.getConnCount())));
        } catch (Exception e) {
            LOGGER.warn("Delete register meta: {} path failed, {}.", meta, e.getCause());
        }
    }

    private RegisterMeta parseRegisterMeta(String data) {
        String[] prams = data.split("/");
        RegisterMeta registerMeta = new RegisterMeta();
        registerMeta.setGroup(prams[2]);
        registerMeta.setVersion(prams[3]);
        registerMeta.setServiceProviderName(prams[4]);

        String[] array = prams[5].split(":");
        registerMeta.setHost(array[0]);
        registerMeta.setPort(Integer.parseInt(array[1]));
        registerMeta.setWeight(Integer.parseInt(array[2]));
        registerMeta.setConnCount(Integer.parseInt(array[3]));

        return registerMeta;
    }

    private ConcurrentSkipListSet<ServiceMeta> getServiceMeta(Address address) {
        ConcurrentSkipListSet<ServiceMeta> serviceMetaSet = serviceMetaMap.get(address);
        if (serviceMetaSet == null) {
            ConcurrentSkipListSet<ServiceMeta> newServiceMetaSet = new ConcurrentSkipListSet<>();
            serviceMetaSet = serviceMetaMap.putIfAbsent(address, newServiceMetaSet);
            if (serviceMetaSet == null) {
                serviceMetaSet = newServiceMetaSet;
            }
        }
        return serviceMetaSet;
    }

    public void destroy() {
        for (PathChildrenCache childrenCache : pathChildrenCaches.values()) {
            try {
                childrenCache.close();
            } catch (IOException ignored) {
            }
        }

        configClient.close();
    }

    public List<RegisterMeta> getAddressByService(ServiceMeta serviceMeta) {
        Pair<Long, List<RegisterMeta>> pairRegistMeta = this.getRegistries().get(serviceMeta);
        List<RegisterMeta> registerMetaList = new ArrayList<>();
        RegisterMeta meta = null;
        for (RegisterMeta registerMeta : pairRegistMeta.getValue()) {
            meta = new RegisterMeta();
            meta.setHost(registerMeta.getHost());
            meta.setPort(registerMeta.getPort());
            meta.setGroup(registerMeta.getGroup());
            meta.setVersion(registerMeta.getVersion());
            meta.setServiceProviderName(registerMeta.getServiceProviderName());
            meta.setWeight(registerMeta.getWeight());
            meta.setConnCount(registerMeta.getConnCount());
            registerMetaList.add(meta);
        }

        return registerMetaList;
    }

}
