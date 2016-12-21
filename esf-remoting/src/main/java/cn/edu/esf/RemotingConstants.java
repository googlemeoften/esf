package cn.edu.esf;

import java.nio.charset.Charset;

/**
 * 远程通信的一些常量
 *
 * @Author heyong
 * @Date 2016/12/15
 */
public class RemotingConstants {
    public static final byte PROCOCOL_VERSION_ESF_REMOTING = 0X0D;

    public static  final byte PROCOCOL_VERSION_HEATBEAT = (byte) 0x0C;

    public static final int DEFAULT_HEARTBEAT_PERIAD = 27;

    public static final int DEFAULT_TIMEOUT = 3000;

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static final String URL_PREFIX = "esf";

}
