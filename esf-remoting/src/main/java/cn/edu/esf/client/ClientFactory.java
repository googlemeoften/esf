package cn.edu.esf.client;

/**
 * @Author heyong
 * @Date 2016/12/14
 */
public interface ClientFactory {
    public Client getClient(final String url) throws Exception;

    public void remove(Client client);
}
