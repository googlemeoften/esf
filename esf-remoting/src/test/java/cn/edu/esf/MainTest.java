package cn.edu.esf;

import cn.edu.esf.utils.ThreadLocalCache;
import org.junit.Test;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public class MainTest{

    @Test
    public void tesThreadLocalCache(){
        String str="hello";
        byte[]bytes = ThreadLocalCache.getBytes(str);
        System.out.println(bytes);
        String s=ThreadLocalCache.getString(bytes);
        System.out.println(s);

    }
}
