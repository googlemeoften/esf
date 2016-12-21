package cn.edu.esf.protocol;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public interface Protocol {

    /**
     * 将IO流转化为Object
     * @param wrapper
     * @param originPos
     * @return
     * @throws Exception
     */
    public Object decode(ByteBufferWrapper wrapper, int originPos) throws Exception;
}
