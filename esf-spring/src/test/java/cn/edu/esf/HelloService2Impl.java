package cn.edu.esf;

import cn.edu.esf.HelloService2;
import cn.edu.esf.Person;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/4/12
 */
public class HelloService2Impl implements HelloService2 {

    @Override
    public String sayHello(Person p) {
        StringBuffer sb = new StringBuffer();
        sb.append("姓名:" + p.getName())
                .append("年龄:" + p.getAge());
        return sb.toString();
    }
}
