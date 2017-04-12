package cn.edu.esf.server;/**
 * Created by HeYong on 2017/2/13.
 */

import cn.edu.esf.config.ConfigurationService;
import cn.edu.esf.exception.ESFException;
import cn.edu.esf.model.matedata.ServiceMetadata;
import cn.edu.esf.pool.ThreadPoolManager;
import cn.edu.esf.remoting.ProviderServer;
import cn.edu.esf.utils.ESFServiceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:该类运行在ESF服务端，负责监听ESF端口，维护服务端线程池、维护业务服务导出的POJO
 *
 * @author heyong
 * @Date 2017/2/13
 */
public class ESFProviderServer implements ProviderServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ESFProviderServer.class);
    private static final String LINESEPARATOR = System.getProperty("line.separator");
    private static final String LINE = "------------------------------------";

    private final ConfigurationService configService = ESFServiceContainer.getInstance(ConfigurationService.class);
    private final RpcRequestProcessor processor = ESFServiceContainer.getInstance(RpcRequestProcessor.class);
    private final Server server = new NettyServer(processor, configService.getBindAddress());
    private final ThreadPoolManager manager = processor.getThreadPoolManager();


    @Override
    public void addMetadata(String serviceUniqueName, ServiceMetadata metadata) {

    }

    @Override
    public void allocThreadPool(String uniqueName, int corePoolSize, int maxPoolSize) throws ESFException {
        manager.allocThreadPool(uniqueName, corePoolSize, maxPoolSize);
    }

    @Override
    public void startHSFServer() throws ESFException {
        try {
            server.start(configService.getESFServerPort());
        } catch (Exception e) {
            throw new ESFException("ESF 服务端启动失败");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(LINE).append(LINESEPARATOR);
        sb.append(" HSF服务器已启动: " + configService.getESFServerPort()).append(LINESEPARATOR);
        sb.append(LINE).append(LINESEPARATOR);
        sb.append(" I/O线程数     : " + (Runtime.getRuntime().availableProcessors() + 1)).append(LINESEPARATOR);
        sb.append(" 处理最小线程数 : " + configService.getESFServerMinPoolSize()).append(LINESEPARATOR);
        sb.append(" 处理最大线程数 : " + configService.getESFServerMaxPoolSize()).append(LINESEPARATOR);
        sb.append(LINE);
        LOGGER.warn(sb.toString());
    }

    @Override
    public void stopHSFServer() throws ESFException {
        server.stop();
    }

//    @Override
//    public void refuseConnect() {
//
//    }
}
