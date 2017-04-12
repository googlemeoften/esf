package cn.edu.esf.remoting;/**
 * Created by HeYong on 2017/2/13.
 */


import cn.edu.esf.exception.ESFException;
import cn.edu.esf.model.matedata.ServiceMetadata;

/**
 * Description:作为ESF提供者的server服务接口
 *
 * @author heyong
 * @Date 2017/2/13
 */
public interface ProviderServer {
    void addMetadata(String serviceUniqueName, ServiceMetadata metadata);

    void allocThreadPool(String uniqueName, int corePoolSize, int maxPoolSize) throws ESFException;

    void startHSFServer() throws ESFException;

    void stopHSFServer() throws ESFException;

   // public void refuseConnect();

}
