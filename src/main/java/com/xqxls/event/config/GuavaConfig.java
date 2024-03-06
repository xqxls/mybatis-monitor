package com.xqxls.event.config;

import com.google.common.eventbus.AsyncEventBus;
import com.xqxls.event.listener.LogInfoListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * @Description:
 * @Author: xqxls
 * @CreateTime: 2024/2/7 9:09
 */
@Configuration
public class GuavaConfig {

    @Bean
    public AsyncEventBus eventBusListener(LogInfoListener listener) {
        AsyncEventBus eventBus = new AsyncEventBus(Executors.newSingleThreadExecutor());
        eventBus.register(listener);
        return eventBus;
    }

}
