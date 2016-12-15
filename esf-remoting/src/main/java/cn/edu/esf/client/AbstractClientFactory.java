package cn.edu.esf.client;

/**
 * @Author heyong
 * @Date 2016/12/14
 */
public abstract class AbstractClientFactory {

    public abstract Client createClient(String url) throws Exception;
}
