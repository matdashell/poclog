package com.poczinha.log.processor.op;

import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.annotation.Ignore;
import com.poczinha.log.processor.annotation.LogEntity;
import com.poczinha.log.processor.mapping.EntityMapping;
import jakarta.persistence.*;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import java.util.*;

public class CollectEntitiesOp {
    public void execute() {
        for (Element entity : Context.entities) {
            System.out.println("Collecting entity: " + entity.getSimpleName());
            LogEntity annotation = entity.getAnnotation(LogEntity.class);
            boolean ignoreForeignKeys = annotation.ignoreForeignKeys();

            Set<Element> fields = new HashSet<>();
            for (Element field : ElementFilter.fieldsIn(entity.getEnclosedElements())) {
                if (isIgnoredField(field) || (ignoreForeignKeys && isIgnoreForeignKey(field))) {
                    continue;
                }
                System.out.println("Collecting field: " + field.getSimpleName());
                fields.add(field);
            }

            Context.mappings.add(new EntityMapping(entity, fields, extractId(entity)));
        }
    }

    private boolean isIgnoredField(Element field) {
        return field.getAnnotation(Ignore.class) != null;
    }

    private boolean isIgnoreForeignKey(Element field) {
        boolean manyToOne = field.getAnnotation(ManyToOne.class) != null;
        boolean oneToMany = field.getAnnotation(OneToMany.class) != null;
        boolean oneToOne = field.getAnnotation(OneToOne.class) != null;
        boolean ManyToMany = field.getAnnotation(jakarta.persistence.ManyToMany.class) != null;

        return manyToOne || oneToMany || oneToOne || ManyToMany;
    }

    private String extractId(Element entity) {
        for (VariableElement field : ElementFilter.fieldsIn(entity.getEnclosedElements())) {
            if (field.getAnnotation(Id.class) != null) {
                return field.getSimpleName().toString();
            }
        }
        throw new RuntimeException("Entity " + entity.getSimpleName() + " does not have an id field");
    }
}
