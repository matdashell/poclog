package com.poczinha.log.bean;

import com.poczinha.log.hibernate.entity.CorrelationEntity;
import com.poczinha.log.hibernate.entity.RegisterEntity;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogSessionRegisterManager {

    @Value("${audit.log.ignoreOnEmptyHeader:true}")
    private boolean ignoreOnEmptyHeader;

    private final List<String> authHeaders = new ArrayList<>();
    private final List<RegisterEntity> registerEntities;
    private final CorrelationEntity correlationEntity;

    public LogSessionRegisterManager() {
        this.registerEntities = new ArrayList<>();
        this.correlationEntity = new CorrelationEntity();
        this.correlationEntity.setIdentifier("anonymous");
    }

    public Object executeAndRegister(ProceedingJoinPoint jp, List<RegisterEntity> registerEntity, Object id) throws Throwable {
        Object result = execute(jp);
        addRegisterEntities(registerEntity, id);
        return result;
    }

    public Object execute(ProceedingJoinPoint jp) throws Throwable {
        try {
            return jp.proceed();
        } catch (Throwable e) {
            registerEntities.clear();
            throw e;
        }
    }

    public void addRegisterEntities(List<RegisterEntity> registerEntity, Object id) {
        this.registerEntities.addAll(registerEntity.stream().peek(entity -> {
            String type = entity.getType();
            entity.setType(type + id);
            entity.setCorrelation(correlationEntity);
        }).collect(Collectors.toList()));
    }

    public boolean canLog() {
        return !this.ignoreOnEmptyHeader || !correlationEntity.getIdentifier().equals("anonymous");
    }

    public List<RegisterEntity> getRegisterEntities() {
        return registerEntities;
    }

    public CorrelationEntity getCorrelationEntity() {
        return correlationEntity;
    }

    public void setIdentifier(String identifier) {
        this.correlationEntity.setIdentifier(identifier);
    }

    public List<String> getAuthHeaders() {
        return authHeaders;
    }

    public void setAuthHeaders(String authHeadrs) {
        this.authHeaders.addAll(Arrays.asList(authHeadrs.split(";")));
    }
}
