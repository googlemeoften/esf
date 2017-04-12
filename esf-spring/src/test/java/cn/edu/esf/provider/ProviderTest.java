package cn.edu.esf.provider;

import cn.edu.esf.model.matedata.ServiceMetadata;
import cn.edu.esf.spring.ESFSpringProviderBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/4/11
 */
public class ProviderTest {
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        String springResourcePath = "provider.xml";
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(springResourcePath);
        ctx.start();
        //System.in.read();
        ESFSpringProviderBean bean = (ESFSpringProviderBean) ctx.getBean("helloProvider");
        ServiceMetadata metadata = bean.getMetaData();
        System.out.println(metadata.getGroup() + "/" + metadata.getGroup() + metadata.getInterfaceName());

    }
}
