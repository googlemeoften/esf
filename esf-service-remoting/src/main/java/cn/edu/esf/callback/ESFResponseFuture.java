package cn.edu.esf.callback;

import cn.edu.esf.RpcResponse;
import cn.edu.esf.domain.ESFResponse;
import cn.edu.esf.exception.ESFException;
import cn.edu.esf.future.ESFFutureListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by HeYong on 2017/1/12.
 */
public class ESFResponseFuture {
    public static ThreadLocal<Future<Object>> futures = new ThreadLocal<>();

    public static ThreadLocal<ESFFutureListener> listener = new ThreadLocal<>();

    public static Object getResponse(long timeout) throws ESFException {
        if (null == futures.get()) {
            throw new ESFException("Thread " + Thread.currentThread() + "haven not set future");
        }

        ESFResponse response = null;
        try {
            response = (ESFResponse) ((RpcResponse) futures.get().get(timeout, TimeUnit.MILLISECONDS)).getResponseObject(null);
        } catch (ExecutionException e) {
            throw new ESFException("ExecutionException", e);
        } catch (TimeoutException e) {
            throw new ESFException("TimeoutException", e);
        } catch (InterruptedException e) {
            throw new ESFException("InterruptedException", e);
        }

        if (response.isError()) {
            throw new ESFException(response.getErrorMsg());
        }
        return response.getResponseObject();
    }

    /**
     * 设置Future方式的调用,不需要应用调用
     *
     */
    public static void setFuture(Future<Object> future) {
        ESFResponseFuture.futures.set(future);
    }

}
