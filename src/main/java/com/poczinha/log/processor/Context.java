package com.poczinha.log.processor;

import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.op.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import java.util.HashSet;
import java.util.Set;

public class Context {

    public static final String BASE = ".audit.log";
    public static final String PACKAGE_CONFIGURATION = BASE + ".configuration";
    public static final String PACKAGE_ASPECT = BASE + ".aspect";
    public static final String PACKAGE_LOG_ENTITIES = BASE + ".service";
    public static final String PACKAGE_RESOLVER_ENTITIES = BASE + ".resolver";

    public static final String ASPECT_NAME = "LogAspect";
    public static final String SERVICE_NAME = "LogService";
    public static final String RESOLVER_NAME = "LogResolver";

    public static CollectEntitiesOp collectEntitiesOp = new CollectEntitiesOp();
    public static CreateEntitiesLogServicesOp createEntitiesLogServicesOp = new CreateEntitiesLogServicesOp();
    public static CreateAspectOp createAspectOp = new CreateAspectOp();
    public static ConfigureOp configureOp = new ConfigureOp();
    public static CreateResolverEntityOp createResolverEntityOp = new CreateResolverEntityOp();

    public static Set<? extends Element> entities;
    public static Set<? extends Element> repositories;
    public static Set<EntityMapping> mappings = new HashSet<>();
    public static String packageName;

    public static Filer filer;

    public static String repositoriesBasePackages;
    public static String entitiesBasePackages;
}
