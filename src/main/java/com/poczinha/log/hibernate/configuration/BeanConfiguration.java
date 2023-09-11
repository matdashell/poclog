package com.poczinha.log.hibernate.configuration;

import com.poczinha.log.hibernate.controler.LogController;
import com.poczinha.log.hibernate.domain.Correlation;
import com.poczinha.log.hibernate.repository.ColumnRepository;
import com.poczinha.log.hibernate.service.ColumnService;
import com.poczinha.log.hibernate.service.RegisterService;
import com.poczinha.log.hibernate.service.TableService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class BeanConfiguration {

    @Bean
    @Scope(
            value = WebApplicationContext.SCOPE_REQUEST,
            proxyMode = ScopedProxyMode.TARGET_CLASS
    )
    public Correlation correlation() {
        return new Correlation();
    }

    @Bean
    public ColumnService columnService() {
        return new ColumnService();
    }

    @Bean
    public TableService tableService() {
        return new TableService();
    }

    @Bean
    public RegisterService registerService() {
        return new RegisterService();
    }

    @Bean
    public LogController logController() {
        return new LogController();
    }
}
