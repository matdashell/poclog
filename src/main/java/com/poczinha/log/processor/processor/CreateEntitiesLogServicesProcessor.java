package com.poczinha.log.processor.processor;

import com.poczinha.log.bean.LogColumnCache;
import com.poczinha.log.hibernate.entity.LogColumnEntity;
import com.poczinha.log.hibernate.entity.LogRegisterEntity;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.mapping.FieldMapping;
import com.poczinha.log.processor.util.Util;
import com.poczinha.log.service.RegisterService;
import com.squareup.javapoet.*;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.poczinha.log.processor.Context.*;
import static com.poczinha.log.processor.util.Util.isNumericType;

public class CreateEntitiesLogServicesProcessor {

    public void execute() {
        for (EntityMapping entity : Context.mappings) {
            String entityName = entity.getEntityName();
            String className = entityName + SERVICE_NAME;

            TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Service.class)
                    .addAnnotation(Util.getGeneratedAnnotation("Service to process entity modifications of " + entityName));

            generateClassFields(builder);
            generateStaticFields(builder, entity);
            createAndUpdateProcessor(builder, entity);
            deleteProcessor(builder, entity);

            Processor.write(builder.build(), Context.PACKAGE_LOG_ENTITIES);
        }
    }

    private void generateStaticFields(TypeSpec.Builder builder, EntityMapping entity) {
        for (FieldMapping field : entity.getFields()) {
            FieldSpec.Builder staticField = FieldSpec.builder(String.class, field.getFieldSnakeCase())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", field.getName());

            builder.addField(staticField.build());
        }

        FieldSpec.Builder tableName = FieldSpec.builder(String.class, "TABLE_NAME")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", entity.getName());

        builder.addField(tableName.build());
    }

    private void generateClassFields(TypeSpec.Builder builder) {
        FieldSpec registerService = Util.buildFieldBean(RegisterService.class, "registerService");
        FieldSpec entityManager = Util.buildFieldBean(EntityManager.class, "entityManager");
        FieldSpec logColumnCache = Util.buildFieldBean(LogColumnCache.class, "logColumnCache");

        builder.addField(registerService);
        builder.addField(entityManager);
        builder.addField(logColumnCache);
    }

    private void deleteProcessor(TypeSpec.Builder builder, EntityMapping entity) {
        MethodSpec.Builder method = buildMethodLogDelete();
        int size = entity.getFields().size();

        method.addStatement("$T columnEntity", LogColumnEntity.class);
        method.addStatement("$T<$T> registers = new $T<>($L)", List.class, LogRegisterEntity.class, ArrayList.class, size);
        method.addCode("\n");

        for (FieldMapping field : entity.getFields()) {
            method.addStatement("columnEntity = logColumnCache.retrieveOrStore(TABLE_NAME, $L)", field.getFieldSnakeCase());
            method.beginControlFlow("if (columnEntity.isActive())");
            method.addStatement("registers.add(registerService.processDelete(columnEntity))");
            method.endControlFlow();
        }

        method.addCode("\n");
        method.addStatement("return registers");

        builder.addMethod(method.build());
    }

    private void createAndUpdateProcessor(TypeSpec.Builder builder, EntityMapping entity) {
        MethodSpec.Builder method = buildMethodLogCreateUpdate(entity);

        FieldMapping id = entity.getId();
        int size = entity.getFields().size();
        String projectionName = entity.getEntityName() + RESOLVER_NAME;
        String packageProjection = Context.packageName + PACKAGE_RESOLVER_ENTITIES;
        ClassName projectionClassName = ClassName.get(packageProjection, projectionName);

        method.addStatement("$T columnEntity", LogColumnEntity.class);
        method.addStatement("$T<$T> registers = new $T<>($L)", List.class, LogRegisterEntity.class, ArrayList.class, size);

        method.addCode("\n");
        method.beginControlFlow("if (request.$L != null)", id.getAccess());
        method.addStatement("$T session = entityManager.unwrap($T.class)", Session.class, Session.class);
        method.addStatement("$T dbEntity = new $T(($T) session, request)", projectionClassName, projectionClassName, SessionImplementor.class);
        method.addCode("\n");

        for (FieldMapping field : entity.getFields()) {
            String fieldAccess = field.getAccess();

            method.addStatement("columnEntity = logColumnCache.retrieveOrStore(TABLE_NAME, $L)", field.getFieldSnakeCase());
            String prefix = "if (columnEntity.isActive() && ";

            if (isNumericType(field.asType())) {
                method.beginControlFlow(prefix + "request.$L != dbEntity.$L)", fieldAccess, fieldAccess);

            } else if (field.asType().getKind().isPrimitive()) {
                method.beginControlFlow(prefix + "$T.nuNotEquals(dbEntity.$L, request.$L))", Util.class, fieldAccess, fieldAccess);

            } else {
                method.beginControlFlow(prefix + "$T.obNotEquals(dbEntity.$L, request.$L))", Util.class, fieldAccess, fieldAccess);
            }

            method.addStatement("registers.add(registerService.processUpdate(columnEntity, $T.valueOf(request.$L)))", Util.class, fieldAccess);
            method.endControlFlow();
        }

        method.nextControlFlow("else");
        method.addCode("\n");

        for (FieldMapping field : entity.getFields()) {
            method.addStatement("columnEntity = logColumnCache.retrieveOrStore(TABLE_NAME, $L)", field.getFieldSnakeCase());
            method.beginControlFlow("if (columnEntity.isActive())");
            method.addStatement("registers.add(registerService.processCreate(columnEntity, $T.valueOf(request.$L)))", Util.class, field.getAccess());
            method.endControlFlow();
        }

        method.endControlFlow();

        method.addCode("\n");
        method.addStatement("return registers");

        builder.addMethod(method.build());
    }

    private static MethodSpec.Builder buildMethodLogCreateUpdate(EntityMapping entity) {
        return MethodSpec.methodBuilder("processLogCreateUpdate")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(List.class, LogRegisterEntity.class))
                .addParameter(entity.getEntityTypeName(), "request");
    }

    private static MethodSpec.Builder buildMethodLogDelete() {
        return MethodSpec.methodBuilder("processLogDelete")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(List.class, LogRegisterEntity.class));
    }
}
