package cn.edu.esf.thread;

/**
 * 线程命名常理类
 *
 * @Author heyong
 * @Date 2016/12/14
 */
public class ThreadNameSpace {
    public static final String ESF_NETTY_BOSS = "ESF-Netty-Boss";

    public static final String ESF_NETTY_WORKER = "ESF-Netty-Worker";

    public static final String ESF_PROCESSER = "ESF-Biz-Processor";

    public static final String getNettyProcessorService(String serviceName) {
        return "ESF-Biz-Processor-" + serviceName;
    }
}
