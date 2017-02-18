package cn.edu.esf;

/**
 * @Author heyong
 * @Date 2016/12/26
 */
public class StringTest {
    public static void main(String[] args) {
        String str = "123.207.127.69:9000/svnadmin";
        int index = str.indexOf("/");
        str = str.substring(0, index);
        System.out.println(str);

    }


}
