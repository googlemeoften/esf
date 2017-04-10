package cn.edu.esf.future;


import java.util.concurrent.Future;

/**
 * 该类用于持有一个remoting的ResponseFuture，供用户使用<br>
 * 替换之前将ReponseFuture存在ThreadLocal中的方式
 */
public class ESFFuture{
    private final Future<Object> future;

    public ESFFuture(Future<Object> future) {
        this.future = future;
    }
}
