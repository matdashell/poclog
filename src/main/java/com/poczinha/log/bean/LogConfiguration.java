package com.poczinha.log.bean;

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
}
