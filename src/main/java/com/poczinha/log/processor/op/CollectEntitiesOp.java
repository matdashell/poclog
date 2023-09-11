package com.poczinha.log.processor.op;

import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.util.Util;

import javax.lang.model.element.Element;

import static com.poczinha.log.processor.util.Util.log;
import static com.poczinha.log.processor.validate.ProcessorValidate.validateEntity;

public class CollectEntitiesOp {
    public void execute() {
        for (Element entity : Context.entities) {

            if (Util.isIgnoreEntity(entity)) continue;
            EntityMapping mapping = new EntityMapping(entity);
            validateEntity(mapping);

            log("Adding mapping for entity {}", mapping.getEntityName());
            Context.mappings.add(mapping);
        }
    }
}
