package com.poczinha.log.bean;

import com.poczinha.log.hibernate.entity.LogCorrelationEntity;
import com.poczinha.log.hibernate.entity.LogRegisterEntity;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LogSessionRegisterManager {

    @Value("${audit.log.ignoreOnEmptyHeader:true}")
    private boolean ignoreOnEmptyHeader;

    private final List<String> authHeaders;
    private final List<LogRegisterEntity> registerEntities;
    private final LogCorrelationEntity correlationEntity;

    public LogSessionRegisterManager() {
        this.authHeaders = new ArrayList<>();
        this.registerEntities = new ArrayList<>();
        this.correlationEntity = new LogCorrelationEntity();
        this.correlationEntity.setIdentifier("anonymous");
    }

    public Object executeAndRegister(ProceedingJoinPoint jp, List<LogRegisterEntity> registerEntity, Object id) throws Throwable {
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

    public void addRegisterEntities(List<LogRegisterEntity> registerEntity, Object id) {
        this.registerEntities.addAll(registerEntity.stream().peek(entity -> {
            entity.setType(entity.getType() + id);
            entity.setCorrelation(correlationEntity);
        }).collect(Collectors.toList()));
    }

    public boolean canLog() {
        return !this.ignoreOnEmptyHeader || !correlationEntity.getIdentifier().equals("anonymous");
    }

    public List<LogRegisterEntity> getRegisterEntities() {
        return registerEntities;
    }

    public LogCorrelationEntity getCorrelationEntity() {
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

    public ListIterator<LogRegisterEntity> getRegisterEntitiesIterator() {
        return registerEntities.listIterator();
    }

    public boolean containsRegisterEntities() {
        return !registerEntities.isEmpty();
    }
}
