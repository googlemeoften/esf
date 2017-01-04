package cn.edu.esf.address;

import java.util.List;

/**
 * 服务地址管理服务，包括了存取地址信息
 * @Author heyong
 * @Date 2017/1/4
 */
public interface AddressService {

    /**
     * 获取目标服务的地址，当没有可用的服务地址的时候，将会返回null
     *
     * @param serviceUniqueName
     *            服务名称
     * @param methodName
     *            方法名称
     * @return 服务地址字符串
     */
    String getServiceAddress(String serviceUniqueName, String methodName, String[] paramTypeStrs, Object[] args);

    /**
     * @param serviceUniqueName
     *            服务名称
     * @return ConfigServer推送下来的原始地址列表，地址列表由传入的服务名称参数指定
     */
    List<String> getServiceAddresses(String serviceUniqueName);
}
