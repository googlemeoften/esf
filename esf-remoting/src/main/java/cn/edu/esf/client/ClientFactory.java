package cn.edu.esf.client;

import cn.edu.esf.RemotingURL;
import cn.edu.esf.exception.ESFException;

/**
 * @Author heyong
 * @Date 2016/12/14
 */
public interface ClientFactory {
    public Client getClient(final RemotingURL url) throws ESFException;

    public void remove(Client client);
}
