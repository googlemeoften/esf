package cn.edu.esf.serialize;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public interface Encoder {

    public byte[] encode(Object obj) throws Exception;
}
