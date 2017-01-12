package cn.edu.esf.async;

import cn.edu.esf.BaseRequest;
import cn.edu.esf.BaseResponse;
import cn.edu.esf.client.Client;

import java.util.concurrent.*;

/**
 * @Author heyong
 * @Date 2016/12/21
 */
public class ResponseCallBackFuture implements Future<Object> {

    private final SendCallBackListener requestCallBackListener;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private final Client client;
    private final int timeout;
    private final long timestamp;
    private final BaseRequest request;

    private BaseResponse response;

    public ResponseCallBackFuture(Client client, int timeout, BaseRequest request) {
        this(null, client, timeout, request);
    }

    public ResponseCallBackFuture(SendCallBackListener requestCallBackListener, Client client, int timeout, BaseRequest request) {
        this.requestCallBackListener = requestCallBackListener;
        this.client = client;
        this.timeout = timeout;
        this.timestamp = System.currentTimeMillis();
        this.request = request;
    }

    public void onResponse(final BaseResponse response) {
        synchronized (this) {
            if (this.response == null) {
                this.client.removeCallback(request.getRequestID());
                this.response = response;
            } else {
                return;
            }
        }

        this.countDownLatch.countDown();

        if (this.requestCallBackListener != null) {
            client.removeCallback(response.getRequestID());
            if (this.requestCallBackListener.getExecutor() != null) {
                this.requestCallBackListener.getExecutor().execute(new Runnable() {
                    public void run() {
                        ResponseCallBackFuture.this.requestCallBackListener.onResponse(response);
                    }
                });
            } else {
                this.requestCallBackListener.onResponse(response);
            }
        }
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        synchronized (this) {
            return this.response != null;
        }
    }

    public BaseRequest getRequest() {
        return request;
    }

    @Override
    public Object get() {

        try {
            this.countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.response;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) {
        if (timeout > 0) {
            try {
                if (!this.countDownLatch.await(timeout, unit)) {
                } else {
                    this.countDownLatch.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.response;
    }


    //    private Object getTruelyObject() {
//        if(this.response== null){
//            throw new ();
//        }
//    }
    public boolean isInvalid(long now) {
        return this.timeout <= 0 || now - this.timestamp > this.timeout;
    }

}
