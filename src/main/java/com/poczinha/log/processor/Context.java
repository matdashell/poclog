package com.poczinha.log.processor;

import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.op.CollectEntitiesOp;
import com.poczinha.log.processor.op.CreateAspectOp;
import com.poczinha.log.processor.op.CreateEntitiesLogServicesOp;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import java.util.HashSet;
import java.util.Set;

public class Context {

    public static CollectEntitiesOp collectEntitiesOp = new CollectEntitiesOp();
    public static CreateEntitiesLogServicesOp createEntitiesLogServicesOp = new CreateEntitiesLogServicesOp();
    public static CreateAspectOp createAspectOp = new CreateAspectOp();

    public static Set<? extends Element> repositories;
    public static Set<EntityMapping> mappings = new HashSet<>();
    public static String packageName;

    public static Filer filer;
}
