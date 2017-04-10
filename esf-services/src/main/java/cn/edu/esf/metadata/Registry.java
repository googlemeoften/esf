package cn.edu.esf.metadata;

public interface Registry {

    /**
     * Establish connections with registry server.
     *
     * 连接注册中心
     *
     * @param connectString list of servers to connect to [host1:port1,host2:port2....]
     */
    void connectToRegistryServer(String connectString);
}
