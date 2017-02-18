package cn.edu.esf.spi;

import cn.edu.esf.config.ConfigurationService;
import cn.edu.esf.utils.ESFServiceContainer;

/**
 * Description:
 *
 * @author heyong
 * @Date 2017/2/18
 */
public class SPITest {
    public static void main(String[] args) {
        ConfigurationService service = ESFServiceContainer.getInstance(ConfigurationService.class);
        System.out.println(service.getPassword());
    }
}
