package com.poczinha.log.bean;

import com.poczinha.log.hibernate.service.ColumnService;
import com.poczinha.log.hibernate.service.CorrelationService;
import com.poczinha.log.hibernate.service.RegisterService;
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
    public Correlation correlation() {
        return new Correlation();
    }

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
    public ColumnService columnService() {
        return new ColumnService();
    }

    @Bean
    public RegisterService registerService() {
        return new RegisterService();
    }

    @Bean
    public LogController logController() {
        return new LogController();
    }

    @Bean
    public CorrelationService correlationService() {
        return new CorrelationService();
    }
}
