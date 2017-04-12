package cn.edu.esf.model.matedata;

import cn.edu.esf.RemoteCallType;
import cn.edu.esf.model.matedata.MethodSpecial;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author heyong
 * @Date 2017/1/4
 */
public class ServiceMetadata implements Serializable {

    private static final long serialVersionUID = -3770364593060366243L;
    private static final String DEFAULT_GROUP = "ESF";
    private static final String DEFAULT_VERSION = "1.0.0";

    private final boolean isProvider;

    private String interfaceName;
    private Class<?> ifClazz = null;
    private String version = DEFAULT_VERSION;
    private String group = DEFAULT_GROUP;
    private String name;
    private String desc;
    private boolean isSupportAsync = false;//是否支持异步
    private String runModel;
    private String proxyStyle = "jdk";

    /**
     * 持有invokeContext的ThreadLocal，任何时候不为空，便于后续流程直接使用
     */
    private ThreadLocal<Serializable> invokeContext = new ThreadLocal<Serializable>();

    /**
     * 回调处理器
     * <p>
     * 每个服务方法对应的回调方法签名有如下约定
     * <p>
     * <pre>
     * public void ${name}_callback(Object invokeContext,Object appResponse, Throwable t);}
     * </pre>
     * <p>
     * 可靠异步时候使用方式 回调发生时，ESF会在这个callbackHandler对象上调用约定的回调方法。
     * 具体描述参见ESFSpringConsumerBean
     */
    private Object callbackHandler;

    /**
     * callback方式调用，用户配置的处理器，支持上下文参数传递，区别于callbackhanlder只是用于reliable，
     * reliablecallback方式。
     */
    private Object callbackInvoker;

    /**
     * 支持自定义回调方法的后缀
     */
    private String callbackMethodSuffix = "_callback";

    private Map<String, AsyncallMethod> asyncallMethods = new HashMap<String, AsyncallMethod>();
    private Map<String, MethodSpecial> methodSpecialMap = new HashMap<String, MethodSpecial>();
    private Map<String, String> serviceProps = new HashMap<String, String>();

    /**
     * 业务线程池大小
     */
    private int consumerMaxPoolSize = 0;
    private AtomicInteger curConsumerMaxPoolSize = null;

    /**
     * 服务端线程池大小
     */
    private int corePoolSize = 0;
    private int maxPoolSize = 0;

    /**
     * 用于设置方法级别的调用超时时间的扩展
     */
    private MethodSpecial[] methodSpecials;

    private transient Object target;
    private volatile transient ClassLoader servicePojoClassLoader;
    private volatile transient String uniqueServiceName;
    // 安全验证的key，对于某些敏感应用需要配置这个值
    private String secureKey;
    // configserver集群站点，用于多注册
    private List<String> configserverCenter;

    public ServiceMetadata(boolean isProvider) {
        this.isProvider = isProvider;
    }

    /**
     * 增加需要异步调用的方法<br>
     * method的格式为：name:方法名;type:调用方式;listener:回调listener类名<br>
     */
    public void addAsyncallMethod(AsyncallMethod asyncFuncDesc) {
        asyncallMethods.put(asyncFuncDesc.getMethodName(), asyncFuncDesc);
    }

    public boolean isProvider() {
        return isProvider;
    }

    public void addProperty(String propName, String propValue) {
        serviceProps.put(propName, propValue);
    }

    public void removeProperty(String propName) {
        serviceProps.remove(propName);
    }

    public AsyncallMethod getAsyncallMethod(String method) {
        return asyncallMethods.get(method.toLowerCase());
    }

    public Object getCallbackHandler() {
        return callbackHandler;
    }

    public String getCallbackMethodSuffix() {
        return callbackMethodSuffix;
    }

    public String getProxyStyle() {
        return proxyStyle;
    }

    public void setProxyStyle(String proxyStyle) {
        this.proxyStyle = proxyStyle;
    }

    public Class<?> getIfClazz() {
        return ifClazz;
    }

    public void setIfClazz(Class<?> ifClazz) {
        this.ifClazz = ifClazz;
        this.servicePojoClassLoader = ifClazz.getClassLoader();
    }

    /**
     * @return 客户端线程池大小
     */
    public int getConsumerMaxPoolSize() {
        return consumerMaxPoolSize;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * @return 当前运行中的线程池大小
     */
    public AtomicInteger getCurConsumerMaxPoolSize() {
        return curConsumerMaxPoolSize;
    }

    /**
     * 获取描述信息
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 获取所属的Group
     */
    public String getGroup() {
        if (null == group || "".equals(group)) {
            return DEFAULT_GROUP;
        }
        return group;
    }


    /**
     * 获取接口名
     */
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * 放置调用上下文的ThreadLocal对象
     * <p>
     * 由服务消费者在调用服务方法前，调用ThreadLocal.set()传入上下文，HSF在调用发生时，
     * 传入的上下文对象只在ReliableCallback方式时使用
     *
     * @return ThreadLocal对象
     */
    public ThreadLocal<Serializable> getInvokeContext() {
        return invokeContext;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public MethodSpecial getMethodSpecial(String methodName) {
        return methodSpecialMap.get(methodName);
    }

    /**
     * 获取简短名称
     */
    public String getName() {
        return name;
    }

    public String getProperty(String propName) {
        return (String) serviceProps.get(propName);
    }

    /**
     * 获取服务属性
     */
    public Map<String, String> getServiceProperties() {
        return serviceProps;
    }

    /**
     * 获取真实的作为服务的object
     */
    public Object getTarget() {
        return target;
    }

    public void initUniqueName() {
        this.uniqueServiceName = interfaceName + ":" + version;
    }

    public String getUniqueName() {
        return uniqueServiceName;
    }

    /**
     * 获取版本信息
     */
    public String getVersion() {
        return version;
    }

    /**
     * 是否为异步调用
     */
    public boolean isAsyncall(String method) {
        return asyncallMethods.containsKey(method.toLowerCase());
    }


    public void setCallbackMethodSuffix(String callbackMethodSuffix) {
        this.callbackMethodSuffix = callbackMethodSuffix;
    }


    public void setCorePoolSize(String corePoolSize) {
        if (corePoolSize != null) {
            try {
                this.corePoolSize = Integer.parseInt(corePoolSize.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Property corePoolSize must be an integer!");
            }
        }
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setInterfaceName(String _interfaceName) {
        interfaceName = _interfaceName;
    }

    public void setInvokeContext(ThreadLocal<Serializable> invokeContext) {
        this.invokeContext = invokeContext;
    }

    public void setMaxPoolSize(String maxPoolSize) {
        if (maxPoolSize != null) {
            try {
                this.maxPoolSize = Integer.parseInt(maxPoolSize.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Property maxPoolSize must be an integer!");
            }
        }
    }

    public ClassLoader getServicePojoClassLoader() {
        return servicePojoClassLoader;
    }

    public void setMethodSpecials(MethodSpecial[] methodSpecials) {
        this.methodSpecials = methodSpecials;
        for (int i = 0; i < methodSpecials.length; i++) {
            methodSpecialMap.put(methodSpecials[i].getMethodName(), methodSpecials[i]);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setVersion(String _version) {
        version = _version;
        checkVersion();
    }

    public Object getCallbackInvoker() {
        return callbackInvoker;
    }

    public void setCallbackInvoker(Object callbackInvoker) {
        this.callbackInvoker = callbackInvoker;
    }

    /**
     * 将异步调用方法的对象信息转化为String
     */
    public String toAsyncallMethodString() {
        StringBuilder amethodString = new StringBuilder();
        for (AsyncallMethod amethod : asyncallMethods.values()) {
            amethodString.append(amethod.toString());
            amethodString.append("&");
        }
        return amethodString.toString();
    }


    @Override
    public String toString() {
        return getUniqueName();
    }

    private void checkVersion() {
        if (version == null || "".equals(version.trim()) || "null".equalsIgnoreCase(version)) {
            throw new IllegalArgumentException("ServiceMetadata.version=" + version);
        }
    }

    public class AsyncallMethod implements Serializable {

        private static final long serialVersionUID = 4680783526459217513L;

        private String methodName;
        private Method method;
        private Class<?> returnType;
        private String type;//异步调用的类型
        private String callback;//回调的callback类名
        private Object callbackInstance;
        private boolean isReliableCallback;

        public String getMethodName() {
            return methodName;
        }

        public Method getMethod() {
            return method;
        }

        public Class<?> getReturnType() {
            return returnType;
        }

        public String getType() {
            return type.toUpperCase();
        }

        public String getCallback() {
            return callback;
        }

        public Object getCallbackInstance() {
            return callbackInstance;
        }

        public boolean isReliable() {
            return "reliable".equalsIgnoreCase(type);
        }

        /**
         * 是否为需要拿到future对象的异步调用
         */
        public boolean isFuture() {
            return RemoteCallType.FUTURE.getCallType().equalsIgnoreCase(type);
        }

        public void setCallback(String callback) {
            this.callback = callback;
        }

        public void setCallbackInstance(Object _callbackInstance) {
            callbackInstance = _callbackInstance;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public void setName(String methodName) {
            this.methodName = methodName.toLowerCase();
        }

        public void setReturnType(Class<?> returnType) {
            this.returnType = returnType;
        }

        public void setType(String type) {
            this.type = type;
        }

//        public void setMethodAttachInvokeContext(String methodName) {
//            this.methodAttachInvoke = methodName;
//        }
//
//        public void setInvokeContext(ThreadLocal<Serializable> invokeContext) {
//            this.invokeContext = invokeContext;
//        }

        /**
         * 转化为可识别的String
         */
        public String toString() {
            StringBuilder strBuilder = new StringBuilder("name:");
            strBuilder.append(methodName);
            strBuilder.append(";type:");
            strBuilder.append(type);
            strBuilder.append(";listener:");
            strBuilder.append(callback);
            return strBuilder.toString();
        }
    }
}
