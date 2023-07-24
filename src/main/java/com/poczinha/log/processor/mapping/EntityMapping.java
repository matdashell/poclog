package com.poczinha.log.processor.mapping;

import javax.lang.model.element.Element;
import java.util.Set;

public class EntityMapping {
    private final Element entity;
    private final Set<Element> fields;
    private final String id;

    public EntityMapping(Element entity, Set<Element> fields, String id) {
        this.entity = entity;
        this.fields = fields;
        this.id = id;
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
}
