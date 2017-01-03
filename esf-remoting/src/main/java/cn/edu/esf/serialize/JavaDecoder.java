package cn.edu.esf.serialize;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * @Author heyong
 * @Date 2016/12/20
 */
public class JavaDecoder implements Decoder {
    @Override
    public Object decode(byte[] bytes, Class<?> classType) throws Exception {
        return this.decode(bytes);
    }

    @Override
    public Object decode(byte[] bytes) throws Exception {

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object result = ois.readObject();

        ois.close();
        return result;
    }
}
