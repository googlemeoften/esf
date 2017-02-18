package cn.edu.esf;

import java.net.SocketAddress;

/**
 * @Author heyong
 * @Date 2016/12/15
 */
public interface Connection {

    public SocketAddress getLocalAddress();

    public SocketAddress getRemotingAddress();

    public void writeResponseToChannel(final BaseResponse response);

    public void refreshLastTime(long lastTime);

}
