package com.poczinha.log.processor.validate;

import com.poczinha.log.processor.mapping.EntityMapping;

public class ProcessorValidate {
    public static void validateEntity(EntityMapping mapping) {
        if (mapping.getId() == null) throw new RuntimeException("Entity " + mapping.getEntityName() + " has no id field");
    }
}
