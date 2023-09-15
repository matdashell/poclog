package com.poczinha.log.hibernate.configuration;

import com.poczinha.log.hibernate.controler.LogController;
import com.poczinha.log.hibernate.domain.Correlation;
import com.poczinha.log.hibernate.domain.SessionLogId;
import com.poczinha.log.hibernate.repository.ColumnRepository;
import com.poczinha.log.hibernate.repository.CorrelationRepository;
import com.poczinha.log.hibernate.repository.RegisterRepository;
import com.poczinha.log.hibernate.repository.TableRepository;
import com.poczinha.log.hibernate.service.ColumnService;
import com.poczinha.log.hibernate.service.CorrelationService;
import com.poczinha.log.hibernate.service.RegisterService;
import com.poczinha.log.hibernate.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;

@Configuration
@EnableAspectJAutoProxy
public class BeanConfiguration {

    @Autowired
    private ApplicationContext context;

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
    public SessionLogId logIdentifier() {
        return new SessionLogId();
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

    @Bean
    public CorrelationService correlationService() {
        return new CorrelationService();
    }

    @Bean
    public BeanProcessor beanProcessor() {
        return new BeanProcessor();
    }

    @Bean
    @DependsOn("entityManagerFactory")
    public ColumnRepository columnRepository() {
        EntityManager em = context.getBean(EntityManager.class);
        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(em);
        return jpaRepositoryFactory.getRepository(ColumnRepository.class);
    }

    @Bean
    @DependsOn("entityManagerFactory")
    public CorrelationRepository correlationRepository() {
        EntityManager em = context.getBean(EntityManager.class);
        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(em);
        return jpaRepositoryFactory.getRepository(CorrelationRepository.class);
    }

    @Bean
    @DependsOn("entityManagerFactory")
    public RegisterRepository registerRepository() {
        EntityManager em = context.getBean(EntityManager.class);
        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(em);
        return jpaRepositoryFactory.getRepository(RegisterRepository.class);
    }

    @Bean
    @DependsOn("entityManagerFactory")
    public TableRepository tableRepository() {
        EntityManager em = context.getBean(EntityManager.class);
        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(em);
        return jpaRepositoryFactory.getRepository(TableRepository.class);
    }
}
