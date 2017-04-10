package cn.edu.esf.serialize;

/**
 * @Author heyong
 * @Date 2017/1/4
 */
public class SerializeTest {
    public static void main(String[] args) throws Exception {
        JavaEncoder encoder = new JavaEncoder();
        String hello = "Hello";
        byte[][] argBytes = new byte[1][];
        argBytes[0] = encoder.encode(hello);
        System.out.println(argBytes[0].length);
    }
}
