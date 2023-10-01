package com.poczinha.log.bean;

import com.poczinha.log.hibernate.entity.RegisterEntity;
import com.poczinha.log.service.ColumnService;
import com.poczinha.log.service.CorrelationService;
import com.poczinha.log.service.RegisterService;
import org.springframework.context.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAspectJAutoProxy
public class LogConfiguration {

    @Bean
    @Scope(
            value = WebApplicationContext.SCOPE_REQUEST,
            proxyMode = ScopedProxyMode.TARGET_CLASS
    )
    public Correlation correlation() {
        return new Correlation("anonymous");
    }

    @Bean
    @Scope(
            value = WebApplicationContext.SCOPE_REQUEST,
            proxyMode = ScopedProxyMode.TARGET_CLASS
    )
    public List<RegisterEntity> registerEntities() {
        return new ArrayList<>();
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

    @Bean
    public LogHeaderInterceptor logHeaderInterceptor() {
        return new LogHeaderInterceptor();
    }

    @Bean
    public RegisterManager registerManager() {
        return new RegisterManager();
    }

    @Bean
    public LogColumnCache logColumnCache() {
        return new LogColumnCache();
    }
}
