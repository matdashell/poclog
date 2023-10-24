package com.poczinha.log.bean;

import com.poczinha.log.service.ColumnService;
import com.poczinha.log.service.CorrelationService;
import com.poczinha.log.service.RegisterService;
import com.poczinha.log.service.TableService;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.context.WebApplicationContext;

@EnableAsync
@Configuration
@EnableAspectJAutoProxy
public class LogConfiguration {

    @Bean
    @Scope(
            value = WebApplicationContext.SCOPE_REQUEST,
            proxyMode = ScopedProxyMode.TARGET_CLASS
    )
    public LogSessionRegisterManager registerManager() {
        return new LogSessionRegisterManager();
    }

    @Bean
    public TableService tableService() {
        return new TableService();
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
    public LogColumnCache logColumnCache(ColumnService columnService) {
        return new LogColumnCache(columnService);
    }

    @Bean
    public LogAuthVerifier authLogFields(LogSessionRegisterManager registerManager) {
        return new LogAuthVerifier(role -> {
            return registerManager.getAuthHeaders().contains(role);
        });
    }
}
