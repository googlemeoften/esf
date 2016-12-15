package cn.edu.esf.client;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public abstract class AbstractClient implements Client {

    public abstract void sendRequest(String str, int timeout) throws Exception;
}
