package com.poczinha.log.processor.op.impl;

import com.poczinha.log.domain.Constants;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.poczinha.log.processor.op.ConfigureOp;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

public class ConfigureOpImpl implements ConfigureOp {

    @Override
    public void execute() {
        createConfiguration();
    }

    private void createConfiguration() {

        TypeSpec.Builder configuration = TypeSpec.classBuilder("LogBeanConfiguration")
                .addAnnotation(Configuration.class);

        AnnotationSpec entityScan = buildAnnotationEntityScan();
        AnnotationSpec repositoryScan = buildAnnotationEnableJpaRepositories();

        configuration.addAnnotation(entityScan);
        configuration.addAnnotation(repositoryScan);

        Processor.write(configuration.build(), Context.PACKAGE_CONFIGURATION);
    }

    private static AnnotationSpec buildAnnotationEnableJpaRepositories() {
        return AnnotationSpec.builder(EnableJpaRepositories.class)
                .addMember("basePackages", "{$S, $S}", Context.repositoriesBasePackages, Constants.LOG_REPOSITORY_SCAN)
                .build();
    }

    private static AnnotationSpec buildAnnotationEntityScan() {
        return AnnotationSpec.builder(EntityScan.class)
                .addMember("basePackages", "{$S, $S}", Context.entitiesBasePackages, Constants.LOG_ENTITY_SCAN)
                .build();
    }
}