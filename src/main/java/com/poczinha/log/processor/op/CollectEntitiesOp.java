package com.poczinha.log.processor.op;

import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.annotation.Ignore;
import com.poczinha.log.processor.mapping.EntityMapping;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.*;

public class CollectEntitiesOp {
    public void execute() {
        for (Element entity : Context.entities) {
            System.out.println("Collecting entity: " + entity.getSimpleName());

            Set<Element> fields = new HashSet<>();
            for (Element field : ElementFilter.fieldsIn(entity.getEnclosedElements())) {
                if (ignoredFields(field)) {
                    continue;
                }
                System.out.println("Collecting field: " + field.getSimpleName());
                fields.add(field);
            }

            Context.mappings.add(new EntityMapping(entity, fields, extractId(entity)));
        }
    }

    private boolean ignoredFields(Element field) {
        boolean ignored = field.getAnnotation(Ignore.class) != null;
        boolean manyToOne = field.getAnnotation(ManyToOne.class) != null;
        boolean oneToMany = field.getAnnotation(OneToMany.class) != null;
        boolean oneToOne = field.getAnnotation(OneToOne.class) != null;
        boolean ManyToMany = field.getAnnotation(javax.persistence.ManyToMany.class) != null;

        return ignored || manyToOne || oneToMany || oneToOne || ManyToMany;
    }

    private Element extractId(Element entity) {
        for (VariableElement field : ElementFilter.fieldsIn(entity.getEnclosedElements())) {
            if (field.getAnnotation(Id.class) != null) {
                return field;
            }
        }
        throw new RuntimeException("Entity " + entity.getSimpleName() + " does not have an id field");
    }
}
