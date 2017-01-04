package cn.edu.esf.config;

import java.io.File;

/**
 * @Author heyong
 * @Date 2017/1/4
 */
public interface ConfigurationService {
    public static final int TEST = 0;//测试
    public static final int REALSE = 1;//运行

    public static final String ESFCONFIG_PROPERTIES_FILE = "config" + File.separator + "esfconfig.properties";
    public static final String ESFSERVER_MIN_POOLSIZE = "esf.server.min.poolsize";
    public static final String ESFSERVER_MAX_POOLSIZE = "esf.server.max.poolsize";
    public static final String ESFSERVER_QUEUE_SIZE = "esf.server.queue.size";
    public static final String ESFSERVER_PORT = "esf.server.port";
    public static final String ESF_VERSION = "esf.version";
    public static final String ESF_PROXY_STYLE = "esf.proxy.version";
    public static final String ESF_SERIALIZER = "esf.serializer";


    public int getESFServerMinPoolSize();

    public int getESFServerMaxPoolSize();

    public int getESFServerPort();

    public int getRunModel();

    public String getESFVersion();



    public void setESFServerMinPoolSize();

    public void setESFServerMaxPoolSize();

    public void setESFServerPort();

    public void setRunModel();



}
