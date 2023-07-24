package com.poczinha.log.processor.op;

import com.poczinha.log.hibernate.controler.LoggerController;
import com.poczinha.log.hibernate.domain.Constants;
import com.poczinha.log.hibernate.mapper.RegisterMapper;
import com.poczinha.log.hibernate.service.ColumnService;
import com.poczinha.log.hibernate.service.IdentifierService;
import com.poczinha.log.hibernate.service.RegisterService;
import com.poczinha.log.hibernate.service.TableService;
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
                .addMember("basePackages", "{$S, $S}", Context.entitiesBasePackage, Constants.ENTITY_SCAN)
                .build();

        AnnotationSpec repositoryScan = AnnotationSpec.builder(EnableJpaRepositories.class)
                .addMember("basePackages", "{$S, $S}", Context.repositoriesBasePackage, Constants.REPOSITORY_SCAN)
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

        MethodSpec.Builder tableService = MethodSpec.methodBuilder("tableService")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Bean.class)
                .returns(TableService.class)
                .addStatement("return new TableService()");

        MethodSpec.Builder identifierService = MethodSpec.methodBuilder("identifierService")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Bean.class)
                .returns(IdentifierService.class)
                .addStatement("return new IdentifierService()");

        MethodSpec.Builder columnService = MethodSpec.methodBuilder("columnService")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Bean.class)
                .returns(ColumnService.class)
                .addStatement("return new ColumnService()");

        MethodSpec.Builder registerMapper = MethodSpec.methodBuilder("registerMapper")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Bean.class)
                .returns(RegisterMapper.class)
                .addStatement("return new RegisterMapper()");

        MethodSpec.Builder loggerController = MethodSpec.methodBuilder("loggerController")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Bean.class)
                .returns(LoggerController.class)
                .addStatement("return new LoggerController()");

        builder.addMethod(registerService.build());
        builder.addMethod(tableService.build());
        builder.addMethod(identifierService.build());
        builder.addMethod(columnService.build());
        builder.addMethod(registerMapper.build());
        builder.addMethod(loggerController.build());

        Processor.write(builder.build());
    }
}
