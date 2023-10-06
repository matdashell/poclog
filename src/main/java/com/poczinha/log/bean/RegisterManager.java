package com.poczinha.log.bean;

import com.poczinha.log.hibernate.entity.RegisterEntity;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegisterManager {

    @Autowired
    private List<RegisterEntity> registerEntities;

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
        }).collect(Collectors.toList()));
    }
}
