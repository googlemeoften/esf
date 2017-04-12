package cn.edu.esf.utils;

import com.sun.management.HotSpotDiagnosticMXBean;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Description:JVM工具类
 *
 * @author heyong
 * @Date 2017/4/12
 */
public class JVMUtils {
    private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";
    private static volatile HotSpotDiagnosticMXBean hotspotMBean;
    private static volatile MemoryMXBean memoryMBean;
    private static Object lock = new Object();

    public static void jstack(OutputStream stream) throws Exception {
        try {
            Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
            Iterator<Map.Entry<Thread, StackTraceElement[]>> ite = map.entrySet().iterator();
            while (ite.hasNext()) {
                Map.Entry<Thread, StackTraceElement[]> entry = ite.next();
                StackTraceElement[] elements = entry.getValue();
                if (elements != null && elements.length > 0) {
                    String threadName = entry.getKey().getName();
                    stream.write(("Thread Name :[" + threadName + "]\n").getBytes());
                    for (StackTraceElement el : elements) {
                        String stack = el.toString() + "\n";
                        stream.write(stack.getBytes());
                    }
                    stream.write("\n".getBytes());
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static double memoryUsed(OutputStream stream) throws Exception {
        try {
            initMemoryMBean();
            stream.write("**********************************Memory Used**********************************\n".getBytes());
            String heapMemoryUsed = memoryMBean.getHeapMemoryUsage().toString() + "\n";
            stream.write(("Heap Memory Used: " + heapMemoryUsed).getBytes());
            String nonHeapMemoryUsed = memoryMBean.getNonHeapMemoryUsage().toString() + "\n";
            stream.write(("NonHeap Memory Used: " + nonHeapMemoryUsed).getBytes());

            return (double) (memoryMBean.getHeapMemoryUsage().getUsed()) / memoryMBean.getHeapMemoryUsage().getMax();
        } catch (Exception e) {
            throw e;
        }
    }

    private static HotSpotDiagnosticMXBean getHotspotMBean() throws Exception {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<HotSpotDiagnosticMXBean>() {
                public HotSpotDiagnosticMXBean run() throws Exception {
                    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                    Set<ObjectName> s = server.queryNames(new ObjectName(HOTSPOT_BEAN_NAME), null);
                    Iterator<ObjectName> itr = s.iterator();
                    if (itr.hasNext()) {
                        ObjectName name = itr.next();
                        HotSpotDiagnosticMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server,
                                name.toString(), HotSpotDiagnosticMXBean.class);
                        return bean;
                    } else {
                        return null;
                    }
                }
            });
        } catch (Exception exp) {
            throw exp;
        }
    }

    private static MemoryMXBean getMemoryMBean() throws Exception {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<MemoryMXBean>() {
                public MemoryMXBean run() throws Exception {
                    return ManagementFactory.getMemoryMXBean();
                }
            });
        } catch (Exception exp) {
            throw exp;
        }
    }

    private static void initHotspotMBean() throws Exception {
        if (hotspotMBean == null) {
            synchronized (lock) {
                if (hotspotMBean == null) {
                    hotspotMBean = getHotspotMBean();
                }
            }
        }
    }

    private static void initMemoryMBean() throws Exception {
        if (memoryMBean == null) {
            synchronized (lock) {
                if (memoryMBean == null) {
                    memoryMBean = getMemoryMBean();
                }
            }
        }
    }
}
