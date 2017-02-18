package cn.edu.esf.client;

import cn.edu.esf.BaseRequest;
import cn.edu.esf.BaseResponse;
import cn.edu.esf.RemotingURL;
import cn.edu.esf.async.ResponseCallBackFuture;
import cn.edu.esf.async.SendCallBackListener;
import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.domain.ESFResponse;
import cn.edu.esf.exception.ESFException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * 负责RPC调用
 *
 * @Author heyong
 * @Date 2016/12/14
 */
public interface Client {
    /**
     * 同步调用
     *
     * @param request
     * @param codecType
     * @param timeout
     * @return
     * @throws Exception
     */
    public BaseResponse syncInvoke(ESFRequest request, byte codecType, int timeout) throws ESFException;

    /**
     * future调用
     *
     * @param request
     * @param codecType
     * @param timeout
     * @return
     * @throws Exception
     */
    public Future<Object> futureInvoke(ESFRequest request, byte codecType, int timeout) throws ESFException;

    /**
     * callback
     *
     * @param request
     * @param listener
     * @param codecType
     * @throws Exception
     */
    public void invokeWithCallBack(final ESFRequest request, final SendCallBackListener listener, final byte codecType)
            throws ESFException;

    public void invokeWithCallBack(final ESFRequest request, final SendCallBackListener listener, final int time,
                                   final byte codecType) throws ESFException;

    public void invokeWithCallBack(BaseRequest request, final SendCallBackListener heartbeatListner)
            throws ESFException;

    public void putResponse(BaseResponse response);

    public ResponseCallBackFuture removeCallback(final Long id);

    public ClientFactory getClientFactory();

    public void close(final String cause);

    public void removeAllInvalidRequestCallBack();

    void removeAllRequestCallBackWhenClose();

    public ConcurrentHashMap<String, Object> getAttributes();

    public boolean isConnected();

    public RemotingURL getUrl();

    public String getLocalAddress();

    public String getRemoteAddress();

}
