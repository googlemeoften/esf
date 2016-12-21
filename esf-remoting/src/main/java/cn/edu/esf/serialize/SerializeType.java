package cn.edu.esf.serialize;

import java.util.ArrayList;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public class SerializeType {

    public static final byte JAVA_SERIALIZE = 1;
    public static final byte HESSION_SERIALIZE = 2;
    public static final byte PROTOBUF_SERIALIZE = 3;

    private static ArrayList<Encoder> encoders = new ArrayList<>();
    private static ArrayList<Decoder> decoders = new ArrayList<>();

    static {
        addDecoder(new JavaDecoder());

        addEncoder(new JavaEncoder());
    }

    public static void addEncoder(Encoder encoder) {
        encoders.add(encoder);
    }

    public static void addDecoder(Decoder decoder) {
        decoders.add(decoder);
    }

    public static Encoder getEncoder(int index) {
        return encoders.get(index - 1);
    }

    public static Decoder getDecoders(int index) {
        return decoders.get(index - 1);
    }
}
