package com.two.service.utils;

import cn.hutool.core.thread.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author dhg
 * @Date 2022/12/19
 */
@Slf4j
@Component
public class ExecutorDynamicService {

    @Value("${executor.corePoolSize}")
    private Integer corePoolSize;

    @Value("${executor.maximumPoolSize}")
    private Integer maximumPoolSize;

    public static final ThreadPoolExecutor EXECUTOR_SERVICE = new ThreadPoolExecutor(5, 10, 10L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(5000), new NamedThreadFactory("TWO-GOODS", false));

    @PostConstruct
    private void init() {
        EXECUTOR_SERVICE.allowCoreThreadTimeOut(true);
        EXECUTOR_SERVICE.setCorePoolSize(corePoolSize);
        EXECUTOR_SERVICE.setMaximumPoolSize(maximumPoolSize);
    }
}
