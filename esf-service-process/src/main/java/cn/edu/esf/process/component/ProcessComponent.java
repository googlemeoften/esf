package cn.edu.esf.process.component;

import cn.edu.esf.exception.ESFException;
import cn.edu.esf.metadata.MetadataService;
import cn.edu.esf.metadata.component.MetadataComponent;
import cn.edu.esf.model.matedata.ServiceMetadata;
import cn.edu.esf.process.ProcessService;
import cn.edu.esf.remoting.GenericService;
import cn.edu.esf.remoting.RPCProtocolService;
import cn.edu.esf.utils.ESFServiceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author heyong
 * @Date 2017/1/8
 */
public class ProcessComponent implements ProcessService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessComponent.class);

    private final MetadataService metadataService = ESFServiceContainer.getInstance(MetadataService.class);
    private final ConcurrentHashMap<String, Object> cachedServices = new ConcurrentHashMap<String, Object>();
    private final RPCProtocolService protocolService = ESFServiceContainer.getInstance(RPCProtocolService.class);

    @Override
    public Object consume(ServiceMetadata metaDate) throws ESFException {

        if (cachedServices.containsKey(metaDate.getUniqueName())) {
            return cachedServices.get(metaDate.getUniqueName());
        }

        Object proxyObject = null;
        List<Class> interfaces = new ArrayList<>(2);

        if (metaDate.getIfClazz() != null) {
            interfaces.add(metaDate.getIfClazz());
        }
        interfaces.add(GenericService.class);

        Class<?>[] interfaceArray = new Class<?>[interfaces.size()];
        interfaces.toArray(interfaceArray);

        proxyObject = createJdkDynamicProxy(metaDate, interfaceArray);

        metadataService.subscribe(metaDate);
        cachedServices.putIfAbsent(metaDate.getUniqueName(),proxyObject);
        return proxyObject;
    }

    @Override
    public void publish(ServiceMetadata metadata) throws ESFException {
        try {
            protocolService.registerProvider(metadata);
        } catch (ESFException e) {
            LOGGER.error("发布ESF服务是出错" + metadata.getUniqueName());
            throw e;
        }
    }

    private <T> T createJdkDynamicProxy(ServiceMetadata metadata, Class<?>[] classes) {
        final Class<?> interfaces = metadata.getIfClazz();
        return (T) Proxy.newProxyInstance(interfaces.getClassLoader(), classes, new ESFServiceProxy(metadata, interfaces));
    }

    public static class ESFServiceProxy implements InvocationHandler {
        private static final String TOSTRING_METHOD = "toString";

        private final RPCProtocolService protocolService = ESFServiceContainer.getInstance(RPCProtocolService.class);
        private final ServiceMetadata metadata;
        // private final String method2attachInvokeContext;


        public ESFServiceProxy(ServiceMetadata metadata, final Class<?> interfaceClass) {
            this.metadata = metadata;
            // this.method2attachInvokeContext = metadata.getProperty()
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if (TOSTRING_METHOD.equals(method.getName())) {
                return proxy.getClass().getName();
            }
            return this.trueInvoke(method, method.getParameterTypes(), args);
        }

        private Object trueInvoke(Method method, Class<?>[] parameterTypes, Object[] args) throws ESFException, Throwable {
            AtomicInteger maxPoolSize = metadata.getCurConsumerMaxPoolSize();
            if (maxPoolSize == null) {
                return protocolService.invoke(metadata, method, method.getName(), parameterTypes, args);
            } else {
                try {
                    int currentSize = maxPoolSize.decrementAndGet();
                    if (currentSize < 0) {
                        String errorMsg = MessageFormat.format("消费线程已经满，service{0}", metadata.getUniqueName());
                        LOGGER.error(errorMsg);
                        throw new ESFException(errorMsg);
                    } else {
                        return protocolService.invoke(metadata, method, method.getName(), parameterTypes, args);
                    }
                } finally {
                    maxPoolSize.incrementAndGet();
                }
            }
        }

    }
}
