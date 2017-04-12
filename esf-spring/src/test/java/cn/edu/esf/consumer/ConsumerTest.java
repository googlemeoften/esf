package cn.edu.esf.consumer;

import cn.edu.esf.HelloService;
import cn.edu.esf.HelloService2;
import cn.edu.esf.Person;
import cn.edu.esf.model.matedata.ServiceMetadata;
import cn.edu.esf.spring.ESFSpringConsumerBean;
import cn.edu.esf.spring.ESFSpringProviderBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/4/11
 */
public class ConsumerTest {
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        String springResourcePath = "consumer.xml";
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(springResourcePath);
        ctx.start();
        HelloService service = (HelloService) ctx.getBean("helloWorldService");

        HelloService2 helloService = (HelloService2) ctx.getBean("helloservice");

        // = (HelloService) bean.getObject();

        for (int i = 0; i < 10; i++) {
            String result = service.sayHello("hello");
            String result2 = helloService.sayHello(new Person("dave", 100));
            System.out.println(result);
            System.out.println(result2);
        }
    }

}
