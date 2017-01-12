package cn.edu.esf.serialize;

import cn.edu.esf.exception.ESFException;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public interface Encoder {

    public byte[] encode(Object obj) throws ESFException;
}
