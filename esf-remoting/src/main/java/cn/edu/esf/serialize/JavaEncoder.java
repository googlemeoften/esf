package cn.edu.esf.serialize;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * @Author heyong
 * @Date 2016/12/20
 */
public class JavaEncoder implements Encoder {
    @Override
    public byte[] encode(Object obj) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.close();

        return bos.toByteArray();
    }
}
