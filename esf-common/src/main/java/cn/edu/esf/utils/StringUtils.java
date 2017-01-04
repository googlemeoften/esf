package cn.edu.esf.utils;

/**
 * @Author heyong
 * @Date 2017/1/2
 */
public class StringUtils {

    public static boolean isBlank(String str) {

        int strLen = 0;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }

        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i))) == false) {
                return false;
            }
        }
        return true;
    }
}
