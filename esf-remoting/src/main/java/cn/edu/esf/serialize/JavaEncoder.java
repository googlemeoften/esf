package cn.edu.esf.serialize;

import cn.edu.esf.exception.ESFException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @Author heyong
 * @Date 2016/12/20
 */
public class JavaEncoder implements Encoder {
    @Override
    public byte[] encode(Object obj) throws ESFException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.close();
        } catch (IOException e) {
            throw new ESFException("encode happened error", e);
        }
        return bos.toByteArray();
    }
}
