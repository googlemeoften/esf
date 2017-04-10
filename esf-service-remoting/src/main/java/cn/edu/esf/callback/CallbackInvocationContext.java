package cn.edu.esf.callback;

public class CallbackInvocationContext {
    private static ThreadLocal<Object> callbackContext = new ThreadLocal<Object>();

    public static void setContext(Object obj) {
        callbackContext.set(obj);
    }

    public static Object getContext() {
        return callbackContext.get();
    }
}