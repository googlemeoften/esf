package cn.edu.esf.metadata;

/**
 * Created by HeYong on 2017/4/5.
 */
public class RegisterMeta implements Cloneable {

    // 地址
    private Address address = new Address();
    // metadata
    private ServiceMeta serviceMeta = new ServiceMeta();
    // 权重 hashCode()与equals()不把weight计算在内
    private volatile int weight;
    // 建议连接数 hashCode()与equals()不把connCount计算在内
    private volatile int connCount;

    public String getHost() {
        return address.getHost();
    }

    public void setHost(String host) {
        address.setHost(host);
    }

    public int getPort() {
        return address.getPort();
    }

    public void setPort(int port) {
        address.setPort(port);
    }

    public String getGroup() {
        return serviceMeta.getGroup();
    }

    public void setGroup(String group) {
        serviceMeta.setGroup(group);
    }

    public String getVersion() {
        return serviceMeta.getVersion();
    }

    public void setVersion(String version) {
        serviceMeta.setVersion(version);
    }

    public String getServiceProviderName() {
        return serviceMeta.getServiceName();
    }

    public void setServiceProviderName(String serviceName) {
        serviceMeta.setServiceName(serviceName);
    }

    public Address getAddress() {
        return address;
    }

    public ServiceMeta getServiceMeta() {
        return serviceMeta;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getConnCount() {
        return connCount;
    }

    public void setConnCount(int connCount) {
        this.connCount = connCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegisterMeta that = (RegisterMeta) o;

        return !(address != null ? !address.equals(that.address) : that.address != null)
                && !(serviceMeta != null ? !serviceMeta.equals(that.serviceMeta) : that.serviceMeta != null);
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (serviceMeta != null ? serviceMeta.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RegisterMeta{" +
                "address=" + address +
                ", serviceMeta=" + serviceMeta +
                ", weight=" + weight +
                ", connCount=" + connCount +
                '}';
    }


    public static class Address {
        private String host;
        private int port;

        public Address() {

        }

        public Address(final String host, final int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public Address setHost(String host) {
            this.host = host;
            return this;
        }

        public int getPort() {
            return port;
        }

        public Address setPort(int port) {
            this.port = port;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Address address = (Address) o;

            return port == address.port && !(host != null ? !host.equals(address.host) : address.host != null);
        }

        @Override
        public int hashCode() {
            int result = host != null ? host.hashCode() : 0;
            result = 31 * result + port;
            return result;
        }
    }

    public static class ServiceMeta {
        //组别
        private String group;
        //版本
        private String version;
        //服务名
        private String serviceName;

        public String getGroup() {
            return group;
        }

        public ServiceMeta setGroup(String group) {
            this.group = group;
            return this;
        }

        public String getVersion() {
            return version;
        }

        public ServiceMeta setVersion(String version) {
            this.version = version;
            return this;
        }

        public String getServiceName() {
            return serviceName;
        }

        public ServiceMeta setServiceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ServiceMeta that = (ServiceMeta) o;

            return !(group != null ? !group.equals(that.group) : that.group != null)
                    && !(version != null ? !version.equals(that.version) : that.version != null)
                    && !(serviceName != null ? !serviceName.equals(that.serviceName) : that.serviceName != null);
        }

        @Override
        public int hashCode() {
            int result = group != null ? group.hashCode() : 0;
            result = 31 * result + (version != null ? version.hashCode() : 0);
            result = 31 * result + (serviceName != null ? serviceName.hashCode() : 0);
            return result;
        }
    }
}


