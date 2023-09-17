package com.poczinha.log.processor;

import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.op.CollectEntitiesOp;
import com.poczinha.log.processor.op.ConfigureOp;
import com.poczinha.log.processor.op.CreateAspectOp;
import com.poczinha.log.processor.op.CreateEntitiesLogServicesOp;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import java.util.HashSet;
import java.util.Set;

public class Context {

    public static final String PACKAGE_CONFIGURATION = "configuration";
    public static final String PACKAGE_ASPECT = "aspect";
    public static final String PACKAGE_LOG_ENTITIES = "processor";


    public static CollectEntitiesOp collectEntitiesOp = new CollectEntitiesOp();
    public static CreateEntitiesLogServicesOp createEntitiesLogServicesOp = new CreateEntitiesLogServicesOp();
    public static CreateAspectOp createAspectOp = new CreateAspectOp();
    public static ConfigureOp configureOp = new ConfigureOp();

    public static Set<? extends Element> repositories;
    public static Set<EntityMapping> mappings = new HashSet<>();
    public static String packageName;

    public static Filer filer;

    public static String idName;
    public static boolean logOnlyIfPresent;
    public static String repositoriesBasePackages;
    public static String entitiesBasePackages;

    public static String logInfos() {
        return "Context {" +
                "repositories=" + repositories +
                ", mappings=" + mappings +
                ", packageName='" + packageName + '\'' +
                ", filer=" + filer +
                ", idName='" + idName + '\'' +
                ", logOnlyIfPresent=" + logOnlyIfPresent +
                ", repositoriesBasePackages='" + repositoriesBasePackages + '\'' +
                ", entitiesBasePackages='" + entitiesBasePackages + '\'' +
                '}';
    }
}
