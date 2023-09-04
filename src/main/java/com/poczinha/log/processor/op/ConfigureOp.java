package com.poczinha.log.processor.op;

import com.poczinha.log.hibernate.controler.LogController;
import com.poczinha.log.hibernate.domain.Constants;
import com.poczinha.log.hibernate.service.RegisterService;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.lang.model.element.Modifier;

public class ConfigureOp {

    public void execute() {

        AnnotationSpec entityScan = AnnotationSpec.builder(EntityScan.class)
                .addMember("basePackages", "{$S, $S}", Context.entitiesBasePackage, Constants.LOG_ENTITY_SCAN)
                .build();

        AnnotationSpec repositoryScan = AnnotationSpec.builder(EnableJpaRepositories.class)
                .addMember("basePackages", "{$S, $S}", Context.repositoriesBasePackage, Constants.LOG_REPOSITORY_SCAN)
                .build();

        TypeSpec.Builder builder = TypeSpec.classBuilder("LogConfiguration")
                .addAnnotation(entityScan)
                .addAnnotation(repositoryScan)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Configuration.class);

        MethodSpec.Builder registerService = MethodSpec.methodBuilder("registerService")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Bean.class)
                .returns(RegisterService.class)
                .addStatement("return new RegisterService()");

        MethodSpec.Builder loggerController = MethodSpec.methodBuilder("LogController")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Bean.class)
                .returns(LogController.class)
                .addStatement("return new LogController()");

        builder.addMethod(registerService.build());
        builder.addMethod(loggerController.build());

        Processor.write(builder.build());
    }
}
