package cn.edu.esf.process.component;/**
 * Created by HeYong on 2017/2/13.
 */

import cn.edu.esf.config.ConfigurationService;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/13
 */
public class ConfigurationComponent implements ConfigurationService {
    @Override
    public int getESFServerMinPoolSize() {
        return 3;
    }

    @Override
    public int getESFServerMaxPoolSize() {
        return 10;
    }

    @Override
    public int getESFServerPort() {
        return 8080;
    }

    @Override
    public int getRunModel() {
        return 0;
    }

    @Override
    public String getESFVersion() {
        return null;
    }

    @Override
    public void setESFServerMinPoolSize() {

    }

    @Override
    public void setESFServerMaxPoolSize() {

    }

    @Override
    public void setESFServerPort() {

    }

    @Override
    public void setRunModel() {

    }

    @Override
    public String getBindAddress() {
        return null;
    }

    @Override
    public String getPassword() {
        return "hello";
    }

    @Override
    public int getThreadPoolQueueSize() {
        return 0;
    }

}
