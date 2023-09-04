package com.poczinha.log.processor.mapping;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.lang.reflect.Type;
import java.util.Set;

public class EntityMapping {
    private final Element entity;
    private final Set<Element> fields;
    private final String id;
    private final TypeMirror idType;

    public EntityMapping(Element entity, Set<Element> fields, Element id) {
        this.entity = entity;
        this.fields = fields;
        this.id = id.getSimpleName().toString();
        this.idType = id.asType();
    }

    public Element getEntity() {
        return entity;
    }

    public Set<? extends Element> getFields() {
        return fields;
    }

    public String getId() {
        return id;
    }

    public TypeMirror getIdType() {
        return idType;
    }
}
