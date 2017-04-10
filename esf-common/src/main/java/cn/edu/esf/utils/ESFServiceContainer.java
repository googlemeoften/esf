package cn.edu.esf.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/13
 */
public class ESFServiceContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ESFServiceContainer.class);
    private final static ConcurrentHashMap<Class<?>, Object> INSTANCE_CACHE = new ConcurrentHashMap<Class<?>, Object>();

    public static <T> T getInstance(Class<T> clazz) {
        T instance = (T) INSTANCE_CACHE.get(clazz);

        if (instance == null) {
            try {
                instance = ServiceLoader.load(clazz, ESFServiceContainer.class.getClassLoader()).iterator().next();
                INSTANCE_CACHE.putIfAbsent(clazz, instance);
                return (T) instance;
            } catch (Throwable e) {
                throw new RuntimeException("can't load " + clazz, e);
            }
        } else {
            if (clazz.isAssignableFrom(instance.getClass())) {
                return instance;
            } else {
                throw new RuntimeException("Init ESFService Container error" + clazz);
            }
        }

    }

    public static <T> List<T> getInstances(Class<T> clazz) {
        List<T> list = (List<T>) INSTANCE_CACHE.get(clazz);

        if (list == null) {
            try {
                list = new ArrayList<>();
                for (T instance : ServiceLoader.load(clazz, ESFServiceContainer.class.getClassLoader())) {
                    list.add(instance);
                }
                INSTANCE_CACHE.putIfAbsent(clazz, list);
                return (List<T>) INSTANCE_CACHE.get(clazz);
            } catch (Throwable e) {
                throw new RuntimeException("can't load " + clazz, e);
            }
        } else {
            if (List.class.isAssignableFrom(list.getClass())) {
                return list;
            } else {
                throw new RuntimeException("[Init HSFService Container Error(List)]" + clazz);
            }
        }
    }

}
