package cn.edu.esf.serialize;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public interface Decoder {
    public Object decode(byte[] bytes, Class<?> classType) throws Exception;

    public Object decode(byte[] bytes) throws Exception;
}
