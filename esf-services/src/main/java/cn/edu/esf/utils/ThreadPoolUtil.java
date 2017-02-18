package cn.edu.esf.utils;

import cn.edu.esf.config.ConfigurationService;
import cn.edu.esf.pool.ThreadPoolManager;

public class ThreadPoolUtil {

  private static final ConfigurationService configService = ESFServiceContainer
      .getInstance(ConfigurationService.class);
  private static final ThreadPoolManager threadPoolManager = new ThreadPoolManager(configService.getESFServerMinPoolSize(),
                                                                            configService.getESFServerMaxPoolSize(), configService.getThreadPoolQueueSize());
  public static ThreadPoolManager getThreadPoolManager() {
    return threadPoolManager;
  }

}