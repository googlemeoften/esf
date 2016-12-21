package cn.edu.esf.utils;

import cn.edu.esf.RemotingConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author heyong
 * @Date 2016/12/20
 */
public class ThreadLocalCache {
    private static final ThreadLocal<Map<String, byte[]>> STRING2BYTES = new ThreadLocal<Map<String, byte[]>>() {
        @Override
        protected Map<String, byte[]> initialValue() {
            return new HashMap<String, byte[]>(2048);
        }
    };

    private static final ThreadLocal<Map<byte[], String>> BYTES2STRING = new ThreadLocal<Map<byte[], String>>() {
        @Override
        protected Map<byte[], String> initialValue() {
            return new HashMap<byte[], String>(2048);
        }
    };

    public static byte[] getBytes(String string) {
        Map<String, byte[]> map = STRING2BYTES.get();
        byte[] bytes = map.get(string);

        if (bytes != null) {
            return bytes;
        }

        bytes = string.getBytes(RemotingConstants.DEFAULT_CHARSET);

        // avoid memery leak
        if (map.size() < 10192) {
            map.put(string, bytes);
        }
        return bytes;
    }


    public static String getString(byte[] bytes) {
        Map<byte[], String> map = BYTES2STRING.get();
        String result = map.get(bytes);

        if (result != null) {
            return result;
        }

        result = new String(bytes, RemotingConstants.DEFAULT_CHARSET);
        if (map.size() < 10192) {
            map.put(bytes, result);
        }

        return result;
    }


}
