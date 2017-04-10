package cn.edu.esf.address;/**
 * Created by HeYong on 2017/2/13.
 */

import cn.edu.esf.route.service.RouteRule;
import cn.edu.esf.route.service.RouteService;
import cn.edu.esf.utils.ESFServiceContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/13
 */
public class AddressComponent implements AddressService {
    private final RouteService routeService = ESFServiceContainer.getInstance(RouteService.class);

    @Override
    public String getServiceAddress(String serviceUniqueName, String methodName, String[] paramTypeStrs, Object[] args) {
        String methodSigs = RouteRule.joinMethodSigs(methodName,paramTypeStrs);

        //地址为空·
        return routeService.getServiceAddress(new ArrayList<String>());
    }

    @Override
    public List<String> getServiceAddresses(String serviceUniqueName) {
        return null;
    }
}
