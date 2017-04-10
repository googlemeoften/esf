package cn.edu.esf.metadata.component;

import cn.edu.esf.metadata.MetadataService;
import cn.edu.esf.model.matedata.ServiceMetadata;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/18
 */
public class MetadataComponent implements MetadataService {
    @Override
    public void publish(ServiceMetadata metadata) {

    }

    @Override
    public boolean republish(ServiceMetadata metadata) {
        return false;
    }

    @Override
    public void subscribe(ServiceMetadata metadata) {

    }

    @Override
    public void unregister(ServiceMetadata metadata) {

    }
}
