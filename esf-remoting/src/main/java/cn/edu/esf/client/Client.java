package cn.edu.esf.client;

import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.domain.ESFResponse;

import java.util.concurrent.Future;

/**
 * 负责RPC调用
 *
 * @Author heyong
 * @Date 2016/12/14
 */
public interface Client {
    public ESFResponse syncInvoke(ESFRequest request, byte codecType, int timeout) throws Exception;

    public Future<Object> futureInvoke(ESFRequest request, byte codecType, int timeout) throws Exception;

}
