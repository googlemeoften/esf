package cn.edu.esf.route.service;/**
 * Created by HeYong on 2017/2/13.
 */

import java.util.List;

/**
 * Description:服务地址路由服务，由路由服务来决定从一堆列表中获取哪个地址
 *
 * @author heyong
 * @Date 2017/2/13
 */
public interface RouteService {

    /**
     * 从参数传入的一堆地址列表中获取可调用的目标地址，当没有可用的服务时，将返回null
     * @param address
     * @return
     */
    String getServiceAddress(List<String> address);
}
