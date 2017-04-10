package cn.edu.esf.route;

import cn.edu.esf.route.service.RouteService;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 描述：随机路由服务组件，负责随机地从服务地址列表中选择目标地址
 * 
 * @author <a href="mailto:bixuan@taobao.com">bixuan</a>
 */
public class RandomRouteComponent implements RouteService {
    public String getServiceAddress(List<String> addresses) {
        if ((addresses == null) || (addresses.size() == 0)) {
            return null;
        }
        int size = addresses.size();
        if (size == 1) {
            return addresses.get(0);
        }
        int index = ThreadLocalRandom.current().nextInt(size);
        return addresses.get(index);
    }
}