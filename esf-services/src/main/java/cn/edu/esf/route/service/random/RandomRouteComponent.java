package cn.edu.esf.route.service.random;

import cn.edu.esf.route.service.RouteService;

import java.util.List;
import java.util.Random;

/**
 * 描述：随机路由服务组件，负责随机地从服务地址列表中选择目标地址
 *
 * @author heyong
 * @Date 2017/2/13
 */
public class RandomRouteComponent implements RouteService {

    private Random random = new Random();

    public String getServiceAddress(List<String> addresses) {
        if ((addresses == null) || (addresses.size() == 0)) {
            return null;
        }
        int size = addresses.size();
        if (size == 1) {
            return addresses.get(0);
        }
        int index = random.nextInt(size);
        return addresses.get(index);
    }
}
