package cn.edu.esf.address;

import cn.edu.esf.exception.ESFException;
import cn.edu.esf.model.matedata.ServiceMetadata;

import java.util.Set;

/**
 * Created by HeYong on 2017/3/3.
 */
public class ZookeeperMetadataAddressService implements MetadataAddressService {
    public static final int DUFAULT_PORT = 2181;


    @Override
    public void publish(ServiceMetadata metadata) throws ESFException {

    }

    @Override
    public void unregisterProvider(ServiceMetadata metadata) {

    }

    @Override
    public void subscribe(ServiceMetadata metadata) throws ESFException {

    }

    @Override
    public void unregisterConsumer(ServiceMetadata metadata) {

    }

    @Override
    public Set<ServiceMetadata> getMetadatas() {
        return null;
    }
}
