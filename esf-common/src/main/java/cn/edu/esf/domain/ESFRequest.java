package cn.edu.esf.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author heyong
 * @Date 2016/12/20
 */
public class ESFRequest implements Serializable {
    private static final long serialVersionUID = 3320218303563383109L;
    private String serviceName;
    private String methodName;
    private String[] methodArgTypes;
    private String[] methosArgs;
    private Map<String, Object> requestProps;
    private transient byte serializeType;
    private transient Class<?>[] parameterClasses;
    private transient Class<?> returnType;


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public String[] getMethodArgTypes() {
        return methodArgTypes;
    }

    public void setMethodArgTypes(String[] methodArgTypes) {
        this.methodArgTypes = methodArgTypes;
    }

    public String[] getMethosArgs() {
        return methosArgs;
    }

    public void setMethosArgs(String[] methosArgs) {
        this.methosArgs = methosArgs;
    }

    public void setRequestProps(String key, Object value) {
        if (requestProps == null) { // 防止反序列化时候，没有初始化
            requestProps = new HashMap<String, Object>(3);
        }
        requestProps.put(key, value);
    }

    public Object getRequestProp(String key) {
        if (requestProps != null) { // 防止反序列化时候，没有初始化
            return requestProps.get(key);
        }
        return null;
    }

    public Class<?>[] getParameterClasses() {
        return parameterClasses;
    }

    public byte getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(byte serializeType) {
        this.serializeType = serializeType;
    }

    public void setParameterClasses(Class<?>[] parameterClasses) {
        this.parameterClasses = parameterClasses;
    }

    public String getMethodKey() {
        StringBuilder methodKeyBuilder = new StringBuilder(serviceName);
        methodKeyBuilder.append(methodName);
        for (int i = 0; i < methodArgTypes.length; i++) {
            methodKeyBuilder.append(methodArgTypes[i]);
        }
        return methodKeyBuilder.toString();
    }

    public Map<String, Object> getRequestProps() {
        return requestProps;
    }

    @Override
    public String toString() {
        return "ESFRequest{" +
                "serviceName='" + serviceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", methodArgTypes=" + Arrays.toString(methodArgTypes) +
                ", methosArgs=" + Arrays.toString(methosArgs) +
                ", requestProps=" + requestProps +
                ", serializeType=" + serializeType +
                ", parameterClasses=" + Arrays.toString(parameterClasses) +
                ", returnType=" + returnType +
                '}';
    }
}
