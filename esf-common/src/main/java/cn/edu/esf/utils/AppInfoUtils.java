package cn.edu.esf.utils;/**
 * Created by HeYong on 2017/2/13.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Description:运行时获得HSF应用的一些基本信息，比如应用名称，服务器类型等等
 *
 * @author heyong
 * @Date 2017/2/13
 */
public class AppInfoUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(AppInfoUtils.class);

    public static final String PARAM_MARKING_PROJECT = "project.name";

    private static final String LINUX_ADMIN_HOME = "/home/admin/";
    private static final String SERVER_TOMCAT = "tomcat";

    private static String appName = null;
    private static String serverType = null;

    // xml方式配置的hsf服务是否启动成功 (spring初始化的所有行为都在同一线程中)
    public static final AtomicBoolean appRunning = new AtomicBoolean(false);
    public static int hsfSpringBeanCountDown = 0;

    static{
        initAppName();
    }

    /**
     * 从Java进程的启动参数中尝试获得应用名称<br />
     * 如果该方法返回<tt>null</tt>，可以尝试使用来获取应用名称
     * <ol>
     * <li>使用<tt>project.name</tt>获得应用名称，获取成功则返回
     * <li>使用服务器启动参数获得应用名称，例如<tt>jboss.server.home.dir</tt>
     * <li>返回<tt>null</tt>
     * </ol>
     *
     * @return <tt>appName</tt> or <tt>null</tt>
     */
    public static String getAppName() {
        return appName;
    }

    public static void initAppName() {
        if (appName != null) {
            return;
        }
        // get from project.name parameter
        appName = System.getProperty(PARAM_MARKING_PROJECT);
        if (appName != null) {
            return;
        }
    }
}
