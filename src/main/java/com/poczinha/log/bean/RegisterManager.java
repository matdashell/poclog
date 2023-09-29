package com.poczinha.log.bean;

import com.poczinha.log.hibernate.entity.RegisterEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegisterManager {

    @Autowired
    private List<RegisterEntity> registerEntities;

    public void addRegisterEntities(List<RegisterEntity> registerEntity, String id) {
        registerEntities.addAll(
                registerEntity.stream()
                        .peek(r -> r.setType(r.getType() + id))
                        .collect(Collectors.toList())
        );
    }

    public void rollback() {
        registerEntities.clear();
    }
}
