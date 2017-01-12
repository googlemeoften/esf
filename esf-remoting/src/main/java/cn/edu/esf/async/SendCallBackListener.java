package cn.edu.esf.async;

        import cn.edu.esf.BaseResponse;

        import java.util.concurrent.Executor;

/**
 * @Author heyong
 * @Date 2016/12/21
 */
public interface SendCallBackListener {

    /**
     * 处理响应
     * @param response
     */
    public void onResponse(BaseResponse response);

    /**
     *  onResponse回调处理线程
     * @return
     */
    public Executor getExecutor();
}
