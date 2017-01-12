package cn.edu.esf.client;

import cn.edu.esf.RemotingURL;
import cn.edu.esf.exception.ESFException;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author heyong
 * @Date 2016/12/14
 */
public abstract class AbstractClientFactory implements ClientFactory{
    private final ConcurrentHashMap<RemotingURL, List<Client>> clients = new ConcurrentHashMap<RemotingURL, List<Client>>();
    private final Random random = new Random(System.currentTimeMillis());


    @Override
    public Client getClient(RemotingURL url) throws ESFException {
        return null;
    }

    @Override
    public void remove(Client client) {

    }

    public abstract Client createClient(RemotingURL url) throws ESFException;
}
