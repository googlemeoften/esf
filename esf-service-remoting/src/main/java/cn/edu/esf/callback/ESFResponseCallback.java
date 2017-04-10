package cn.edu.esf.callback;


import cn.edu.esf.exception.ESFException;

public interface ESFResponseCallback {
    /**
     * 当对端业务层抛出异常，HSF层将回调该方法。
     * 
     * @param t 对端业务层抛出的异常
     */
    public void onAppException(Throwable t);

    /**
     * 当对端业务层正常返回结果，HSF层将回调该方法。
     */
    public void onAppResponse(Object appResponse);

    /**
     * 当HSF层出现异常时，回调该方法。
     */
    public void onHSFException(ESFException exception);
}