package cn.edu.esf;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/18
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String str) {
        return "hello"+str;
    }
}
