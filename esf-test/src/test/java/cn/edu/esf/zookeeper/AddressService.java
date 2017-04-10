package cn.edu.esf.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by HeYong on 2017/2/20.
 */
public class AddressService {

    private final CuratorFramework client;


    public AddressService(String hostName) {
        this.client = CuratorFrameworkFactory.newClient(hostName, new RetryNTimes(3, 5000));
        client.start();
    }

    public void creatPath(String path, byte[] datas) throws Exception {
        client.create().forPath(path, datas);
        ExecutorService pool = Executors.newCachedThreadPool();
        PathChildrenCache watch = new PathChildrenCache(client, path, true);
        watch.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {

                ChildData data = event.getData();

                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("child add: " + data.getPath() + data.getData());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("chile remove：" + data.getPath());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("child update:" + data.getPath()+data.getData());
                        break;
                    default:
                        break;
                }
            }
        });

        watch.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        Thread.sleep(Integer.MAX_VALUE);
    }
}
