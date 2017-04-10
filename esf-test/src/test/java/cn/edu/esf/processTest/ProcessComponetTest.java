package cn.edu.esf.processTest;

import cn.edu.esf.HelloService;
import cn.edu.esf.HelloServiceImpl;
import cn.edu.esf.exception.ESFException;
import cn.edu.esf.model.matedata.ServiceMetadata;
import cn.edu.esf.process.component.ProcessComponent;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/22
 */
public class ProcessComponetTest {

    private static ProcessComponent pc = new ProcessComponent();

    public static void main(String[] args) throws ESFException {

        for (int i = 0; i < 10; i++) {
            ServiceMetadata metadata = new ServiceMetadata(false);

            Class clazz = HelloServiceImpl.class;
            String targetInstance = clazz.getName();
            metadata.setInterfaceName(targetInstance);
            metadata.setIfClazz(HelloService.class);
            metadata.initUniqueName();

            HelloService obj = (HelloService) pc.consume(metadata);

            Object result = obj.sayHello("world");
            System.out.println(result);
        }
    }
}
