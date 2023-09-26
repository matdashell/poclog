package com.poczinha.log.bean;

import com.poczinha.log.bean.configuration.KafkaProducerConfig;
import com.poczinha.log.bean.manager.SessionIdentifier;
import com.poczinha.log.bean.manager.TypeCountManager;
import com.poczinha.log.service.KafkaProducerService;
import org.springframework.context.annotation.*;
import org.springframework.web.context.WebApplicationContext;

@Configuration
@EnableAspectJAutoProxy
public class LogConfiguration {

    @Bean
    @Scope(
            value = WebApplicationContext.SCOPE_REQUEST,
            proxyMode = ScopedProxyMode.TARGET_CLASS
    )
    public SessionIdentifier sessionIdentifier() {
        return new SessionIdentifier();
    }

    @Bean
    @Scope(
            value = WebApplicationContext.SCOPE_REQUEST,
            proxyMode = ScopedProxyMode.TARGET_CLASS
    )
    public TypeCountManager typeCountManager() {
        return new TypeCountManager();
    }

    @Bean
    public KafkaProducerConfig kafkaProducerConfig() {
        return new KafkaProducerConfig();
    }

    @Bean
    public KafkaProducerService kafkaProducerService() {
        return new KafkaProducerService();
    }
}
