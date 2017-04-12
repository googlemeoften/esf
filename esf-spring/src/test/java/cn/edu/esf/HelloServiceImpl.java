package cn.edu.esf;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/4/11
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String say) {
        return "hello---------" + say;
    }
}
