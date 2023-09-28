package com.poczinha.log.processor;

import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.op.CollectEntitiesOp;
import com.poczinha.log.processor.op.ConfigureOp;
import com.poczinha.log.processor.op.CreateAspectOp;
import com.poczinha.log.processor.op.CreateEntitiesLogServicesOp;
import com.poczinha.log.processor.op.impl.CollectEntitiesOpImpl;
import com.poczinha.log.processor.op.impl.ConfigureOpImpl;
import com.poczinha.log.processor.op.impl.CreateAspectOpImpl;
import com.poczinha.log.processor.op.impl.CreateEntitiesLogServicesOpImpl;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import java.util.HashSet;
import java.util.Set;

public class Context {

    public static final String PACKAGE_CONFIGURATION = "configuration";
    public static final String PACKAGE_ASPECT = "aspect";
    public static final String PACKAGE_LOG_ENTITIES = "processor";

    public static CollectEntitiesOp collectEntitiesOp = new CollectEntitiesOpImpl();
    public static CreateEntitiesLogServicesOp createEntitiesLogServicesOp = new CreateEntitiesLogServicesOpImpl();
    public static CreateAspectOp createAspectOp = new CreateAspectOpImpl();
    public static ConfigureOp configureOp = new ConfigureOpImpl();

    public static Set<? extends Element> repositories;
    public static Set<EntityMapping> mappings = new HashSet<>();
    public static String packageName;

    public static Filer filer;

    public static String repositoriesBasePackages;
    public static String entitiesBasePackages;
}
