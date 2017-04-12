package cn.edu.esf.metadata.component;

import cn.edu.esf.config.ConfigurationService;
import cn.edu.esf.metadata.MetadataService;
import cn.edu.esf.metadata.NotifyListener;
import cn.edu.esf.metadata.RegisterMeta;
import cn.edu.esf.model.matedata.ServiceMetadata;
import cn.edu.esf.utils.ESFServiceContainer;
import cn.edu.esf.utils.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/18
 */
public class MetadataComponent implements MetadataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataComponent.class);
    private final ZookeeperRegistryService registryService = new ZookeeperRegistryService();
    private final ConfigurationService configService = ESFServiceContainer.getInstance(ConfigurationService.class);

    public MetadataComponent() {
        String zookeeperConnect = configService.getRegistryAddress();
        if (null == zookeeperConnect) {
            zookeeperConnect = "127.0.0.1:2181";
        }
        registryService.connectToRegistryServer(zookeeperConnect);
    }

    @Override
    public void publish(ServiceMetadata metadata) {

        registryService.doRegister(transform(metadata));
    }


    @Override
    public void subscribe(ServiceMetadata metadata) {
        registryService.subscribe(transform(metadata).getServiceMeta(), new NotifyListener() {
            @Override
            public void notify(RegisterMeta registerMeta, NotifyEvent event) {

            }
        });
    }

    @Override
    public void unregister(ServiceMetadata metadata) {
        registryService.doUnregister(transform(metadata));
    }

    private RegisterMeta transform(ServiceMetadata metadata) {
        RegisterMeta meta = new RegisterMeta();
        meta.setHost(NetUtil.getLocalAddress());
        meta.setPort(configService.getESFServerPort());
        meta.setVersion(metadata.getVersion());
        meta.setGroup(metadata.getGroup());
//        meta.setWeight();
//        meta.setConnCount(metadata.);
        return meta;
    }

    public List<String> getServiceAddress(ServiceMetadata metadata) {
        return null;
    }
}
