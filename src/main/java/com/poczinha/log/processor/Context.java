package com.poczinha.log.processor;

import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.op.*;
import com.poczinha.log.processor.op.impl.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import java.util.HashSet;
import java.util.Set;

public class Context {

    public static final String BASE = ".audit.log";
    public static final String PACKAGE_CONFIGURATION = BASE + ".configuration";
    public static final String PACKAGE_ASPECT = BASE + ".aspect";
    public static final String PACKAGE_LOG_ENTITIES = BASE + ".service";
    public static final String PACKAGE_PROJECTION_ENTITIES = BASE + ".projection";

    public static CollectEntitiesOp collectEntitiesOp = new CollectEntitiesOpImpl();
    public static CreateEntitiesLogServicesOp createEntitiesLogServicesOp = new CreateEntitiesLogServicesOpImpl();
    public static CreateAspectOp createAspectOp = new CreateAspectOpImpl();
    public static ConfigureOp configureOp = new ConfigureOpImpl();
    public static CreateProjectionEntityOp createProjectionEntitiesOp = new CreateProjectionEntityOpImpl();

    public static Set<? extends Element> repositories;
    public static Set<EntityMapping> mappings = new HashSet<>();
    public static String packageName;

    public static Filer filer;

    public static String repositoriesBasePackages;
    public static String entitiesBasePackages;
}
