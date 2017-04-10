package cn.edu.esf.metadata;

import java.util.Collection;
import static cn.edu.esf.metadata.RegisterMeta.*;

public interface RegistryService extends Registry {

    /**
     * Register Service to registry server.
     */
    void publish(RegisterMeta meta);

    /**
     * Unregister service to registry server.
     */
    void unpublish(RegisterMeta meta);

    /**
     * Subscribe a service from registry server.
     */
    void subscribe(ServiceMeta serviceMeta, NotifyListener listener);

    /**
     * Find a service in the local scope.
     */
    Collection<RegisterMeta> lookup(ServiceMeta serviceMeta);
}
