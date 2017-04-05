package cn.edu.esf.metadata;

/**
 * Created by HeYong on 2017/4/5.
 */
public class RegisterMeta {


    public static class Address {
        private String host;
        private int port;

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

    public static class ServiceMeta{
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


