package cn.edu.esf.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 生成唯一UUID
 *
 * @Author heyong
 * @Date 2016/12/14
 */
public class UUIDGenerator {
    private static AtomicLong opaque = new AtomicLong();

    public static final synchronized long getNextOpaque() {
        return opaque.incrementAndGet();
    }

}