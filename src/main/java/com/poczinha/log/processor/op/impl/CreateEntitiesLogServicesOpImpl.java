package com.poczinha.log.processor.op.impl;

import com.poczinha.log.hibernate.entity.RegisterEntity;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.mapping.FieldMapping;
import com.poczinha.log.processor.op.CreateEntitiesLogServicesOp;
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

import static com.poczinha.log.processor.Context.RESOLVER_NAME;
import static com.poczinha.log.processor.Context.SERVICE_NAME;
import static com.poczinha.log.processor.util.Util.isNumericType;

public class CreateEntitiesLogServicesOpImpl implements CreateEntitiesLogServicesOp {

    @Override
    public void execute() throws ClassNotFoundException {
        for (EntityMapping entity : Context.mappings) {
            String className = entity.getEntityName() + SERVICE_NAME;

            TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Service.class);

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
    }

    private void generateClassFields(TypeSpec.Builder builder) {
        FieldSpec registerService = Util.buildFieldBean(RegisterService.class, "registerService");
        FieldSpec entityManager = Util.buildFieldBean(EntityManager.class, "em");

        builder.addField(registerService);
        builder.addField(entityManager);
    }

    private void deleteProcessor(TypeSpec.Builder builder, EntityMapping entity) {
        MethodSpec.Builder method = buildMethodLogDelete(entity);
        int size = entity.getFields().size();

        method.addStatement("$T<$T> reg = new $T<>($L)", List.class, RegisterEntity.class, ArrayList.class, size);
        method.addCode("\n");

        for (FieldMapping field : entity.getFields()) {
            method.addStatement("reg.add(registerService.processDelete($L))", field.getFieldSnakeCase());
        }

        method.addCode("\n");
        method.addStatement("return reg");

        builder.addMethod(method.build());
    }

    private void createAndUpdateProcessor(TypeSpec.Builder builder, EntityMapping entity) {
        MethodSpec.Builder method = buildMethodLogCreateUpdate(entity);

        FieldMapping id = entity.getId();
        int size = entity.getFields().size();
        String projectionName = entity.getEntityName() + RESOLVER_NAME;
        String packageProjection = Context.packageName + Context.PACKAGE_RESOLVER_ENTITIES;
        ClassName projectionClassName = ClassName.get(packageProjection, projectionName);

        method.addStatement("$T<$T> reg = new $T<>($L)", List.class, RegisterEntity.class, ArrayList.class, size);

        method.addCode("\n");
        method.beginControlFlow("if (req.$L != null)", id.getAccess());
        method.addStatement("$T session = em.unwrap($T.class)", Session.class, Session.class);
        method.addStatement("$T dbe = new $T(($T) session, req)", projectionClassName, projectionClassName, SessionImplementor.class);
        method.addCode("\n");

        for (FieldMapping field : entity.getFields()) {
            String fieldAccess = field.getAccess();

            if (field.asType().getKind().isPrimitive()) {
                method.beginControlFlow("if (req.$L != dbe.$L)", fieldAccess, fieldAccess);

            } else if (isNumericType(field.asType())) {
                method.beginControlFlow("if ($T.nuNotEquals(dbe.$L, req.$L))", Util.class, fieldAccess, fieldAccess);

            } else {
                method.beginControlFlow("if ($T.obNotEquals(dbe.$L, req.$L))", Util.class, fieldAccess, fieldAccess);
            }

            method.addStatement("reg.add(registerService.processUpdate($L, $T.valueOf(req.$L)))", field.getFieldSnakeCase(), Util.class, fieldAccess);
            method.endControlFlow();
        }

        method.nextControlFlow("else");
        method.addCode("\n");

        for (FieldMapping field : entity.getFields()) {
            method.addStatement("reg.add(registerService.processCreate($L, $T.valueOf(req.$L)))", field.getFieldSnakeCase(), Util.class, field.getAccess());
        }

        method.endControlFlow();

        method.addCode("\n");
        method.addStatement("return reg");

        builder.addMethod(method.build());
    }

    private static MethodSpec.Builder buildMethodLogCreateUpdate(EntityMapping entity) {
        return MethodSpec.methodBuilder("processLogCreateUpdate")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(List.class, RegisterEntity.class))
                .addParameter(entity.getEntityTypeName(), "req");
    }

    private static MethodSpec.Builder buildMethodLogDelete(EntityMapping entity) {
        return MethodSpec.methodBuilder("processLogDelete")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(List.class, RegisterEntity.class));
    }
}
