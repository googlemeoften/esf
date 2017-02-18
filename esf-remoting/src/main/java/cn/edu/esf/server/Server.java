package cn.edu.esf.server;

/**
 * 服务端接口
 * @Author heyong
 * @Date 2016/12/14
 */
public interface Server {

    /**
     * 启动服务端
     * @param listenPort
     */
    public void start(int listenPort) throws Exception;

    /**
     * 关闭
     */
    public void stop();


}
