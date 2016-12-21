package cn.edu.esf.client;

import cn.edu.esf.BaseRequest;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public abstract class AbstractClient implements Client {

    public abstract void sendRequest(BaseRequest request, int timeout) throws Exception;
}
