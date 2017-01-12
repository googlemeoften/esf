package cn.edu.esf.client;

import cn.edu.esf.*;
import cn.edu.esf.async.ChannelFutureWrapper;
import cn.edu.esf.async.ResponseCallBackFuture;
import cn.edu.esf.async.SendCallBackListener;
import cn.edu.esf.domain.ESFRequest;
import cn.edu.esf.exception.ESFException;
import cn.edu.esf.utils.RemotingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public abstract class AbstractClient implements Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractClient.class);

    private final RemotingURL remotingURL;
    private final int timeout;
    protected final ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();//和client相关的属性
    protected final ConcurrentHashMap<Long, ResponseCallBackFuture> responses = new ConcurrentHashMap<>();

    public AbstractClient(RemotingURL remotingURL, int timeout) {
        this.remotingURL = remotingURL;
        this.timeout = timeout;
    }

    @Override
    public Object syncInvoke(ESFRequest request, byte codecType, int timeout) throws ESFException {
        timeout = validateTimeout(timeout);
        RpcRequest wapper = RemotingUtil.convert2RpcRequest(request, codecType, timeout);
        ResponseCallBackFuture responseCallBack = new ResponseCallBackFuture(this, timeout, wapper);
        sendRequest(wapper, timeout);

        return responseCallBack.get(timeout, TimeUnit.MILLISECONDS);
    }


    @Override
    public Future<Object> futureInvoke(ESFRequest request, byte codecType, int timeout) throws ESFException {
        timeout = validateTimeout(timeout);

        BaseRequest wapper = RemotingUtil.convert2RpcRequest(request, codecType, timeout);
        ResponseCallBackFuture responseCallBack = new ResponseCallBackFuture(this, timeout, wapper);
        responses.put(wapper.getRequestID(), responseCallBack);
        sendRequest(wapper, timeout);
        return responseCallBack;
    }


    @Override
    public void invokeWithCallBack(ESFRequest request, SendCallBackListener listener, byte codecType) throws ESFException {
        this.invokeWithCallBack(request, listener, 1000, codecType);
    }

    @Override
    public void invokeWithCallBack(ESFRequest request, SendCallBackListener listener, int time, byte codecType) throws ESFException {
        BaseRequest wapper = RemotingUtil.convert2RpcRequest(request, codecType, timeout);
        ResponseCallBackFuture responseCallBack = new ResponseCallBackFuture(listener, this, timeout, wapper);
        responses.put(wapper.getRequestID(), responseCallBack);

        //this.startTiming(request, timeout);
        this.sendRequest(wapper, timeout);
    }

    @Override
    public void invokeWithCallBack(BaseRequest request, SendCallBackListener heartbeatListner) throws ESFException {
        ResponseCallBackFuture future = new ResponseCallBackFuture(heartbeatListner, this, timeout, request);
        responses.put(request.getRequestID(), future);
        this.startTiming(request, RemotingConstants.DEFAULT_TIMEOUT);
        this.sendRequest(request, RemotingConstants.DEFAULT_TIMEOUT);
    }

    /**
     * 验证超时时间是否合法
     *
     * @param timeout
     * @return
     */
    private int validateTimeout(int timeout) {
        if (timeout <= 0) {
            timeout = RemotingConstants.DEFAULT_TIMEOUT;
        }
        return timeout;
    }

    public void putResponse(BaseResponse wrapper) {
//        RemotingRuntimeInfoHolder.getInstance().increasecountReceivedResponses();
        ResponseCallBackFuture responseCallBack = responses.get(wrapper.getRequestID());
        if (responseCallBack == null) {
            LOGGER.warn(this + "## give up the response,request id is:" + wrapper.getRequestID()
                    + ",maybe because timeout! " + wrapper);
        } else {
            responseCallBack.onResponse(wrapper);
        }
    }

    @Override
    public void close(String cause) {
        removeAllRequestCallBackWhenClose();
        doClose(cause);
    }

    public ResponseCallBackFuture removeCallback(final Long id) {
        return responses.remove(id);
    }

    @Override
    public void removeAllInvalidRequestCallBack() {
        final Set<Long> removeRequestSet = new HashSet<>();
        final long now = System.currentTimeMillis();

        for (Map.Entry<Long, ResponseCallBackFuture> entry : this.responses.entrySet()) {
            if (entry.getValue().isInvalid(now)) {
                removeRequestSet.add(entry.getKey());
            }
        }

        int count = 0;
        for (Long requestID : removeRequestSet) {
            ResponseCallBackFuture future = this.responses.get(requestID);
            if (future != null && future.isInvalid(now)) {
                BaseResponse reponse = future.getRequest().createErrorResponse(
                        "invalid callback is removed because of timeout");
                reponse.setStatus(ResponseStatus.CLIENT_TIMEOUT);
                // 让callBack超时
                future.onResponse(reponse);
                count++;
            }
        }
    }

    @Override
    public void removeAllRequestCallBackWhenClose() {
        int count = 0;
        for (final Map.Entry<Long, ResponseCallBackFuture> entry : this.responses.entrySet()) {
            // 再次确认
            final ResponseCallBackFuture requestCallBack = entry.getValue();
            BaseResponse reponse = requestCallBack.getRequest().createErrorResponse(
                    "invalid callback is removed because of connection closed");
            reponse.setStatus(ResponseStatus.SERVER_ERROR);
            // 让callBack超时
            requestCallBack.onResponse(reponse);
            count++;
        }
        LOGGER.debug("移除" + count + "个无效回调（断连）");
    }

    @Override
    public ConcurrentHashMap<String, Object> getAttributes() {
        return attributes;
    }

    public RemotingURL getRemotingURL() {
        return remotingURL;
    }

    public int getTimeout() {
        return timeout;
    }

    @Override
    public RemotingURL getUrl() {
        return this.remotingURL;
    }

    /**
     * 依赖底层的NIO(netty,mina)等的实现
     */
    public abstract ChannelFutureWrapper sendRequest(BaseRequest wrapper, int timeout) throws ESFException;

    /**
     * 处理超时的请求
     * @param request
     * @param timeout
     */
    public abstract void startTiming(final BaseRequest request, final int timeout);

    /**
     * 依赖底层的NIO(netty,mina)等的资源清理
     */
    protected abstract void doClose(String cause);
}
