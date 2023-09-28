package com.poczinha.log.processor.op.impl;

import com.poczinha.log.bean.TypeCountManager;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.mapping.FieldMapping;
import com.poczinha.log.processor.op.CreateEntitiesLogServicesOp;
import com.poczinha.log.processor.util.Util;
import com.poczinha.log.service.RegisterService;
import com.squareup.javapoet.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.persistence.EntityManager;

import static com.poczinha.log.processor.util.Util.isNumericType;

public class CreateEntitiesLogServicesOpImpl implements CreateEntitiesLogServicesOp {

    @Override
    public void execute() {
        for (EntityMapping entity : Context.mappings) {
            String className = entity.getEntityName() + "LogService";

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

        FieldSpec typCountManager = Util.buildFieldBean(TypeCountManager.class, "typeCountManager");
        FieldSpec registerService = Util.buildFieldBean(RegisterService.class, "registerService");
        FieldSpec entityManager = Util.buildFieldBean(EntityManager.class, "em");

        builder.addField(typCountManager);
        builder.addField(registerService);
        builder.addField(entityManager);
    }

    private void deleteProcessor(TypeSpec.Builder builder, EntityMapping entity) {
        MethodSpec.Builder method = buildMethodLogDelete(entity);
        TypeMirror typeEtity = entity.asType();

        method.addStatement("typeCountManager.countDeletion()");
        method.addStatement("$T dbEntity = em.find($T.class, entityId)", typeEtity, typeEtity);
        method.addCode("\n");

        for (FieldMapping field : entity.getFields()) {
            method.addStatement("registerService.registerDelete($L, $T.valueOf(dbEntity.$L))", field.getFieldSnakeCase(), Util.class, field.getAccess());
        }

        builder.addMethod(method.build());
    }

    private void createAndUpdateProcessor(TypeSpec.Builder builder, EntityMapping entity) {

        MethodSpec.Builder method = buildMethodLogCreateUpdate(entity);
        FieldMapping id = entity.getId();

        TypeName entityTypeName = entity.getEntityTypeName();
        String access = id.getAccess();

        method.addCode("\n");
        method.beginControlFlow("if (currentEntity.$L != null)", access);
        method.addStatement("typeCountManager.countModification()");
        method.addStatement("$T dbEntity = em.find($T.class, currentEntity.$L)", entityTypeName, entityTypeName, access);
        method.addCode("\n");

        for (FieldMapping field : entity.getFields()) {
            String fieldAccess = field.getAccess();

            if (field.asType().getKind().isPrimitive()) {
                method.beginControlFlow("if (currentEntity.$L != dbEntity.$L)", fieldAccess, fieldAccess);

            } else if (isNumericType(field.asType())) {
                method.beginControlFlow("if ($T.nuNotEquals(dbEntity.$L, currentEntity.$L))", Util.class, fieldAccess, fieldAccess);

            } else {
                method.beginControlFlow("if ($T.obNotEquals(dbEntity.$L, currentEntity.$L))", Util.class, fieldAccess, fieldAccess);
            }

            method.addStatement("registerService.registerUpdate($L, $T.valueOf(dbEntity.$L), Util.valueOf(currentEntity.$L))", field.getFieldSnakeCase(), Util.class, fieldAccess, fieldAccess);
            method.endControlFlow();
        }

        method.nextControlFlow("else");

        method.addStatement("typeCountManager.countCreation()");
        method.addCode("\n");

        for (FieldMapping field : entity.getFields()) {
            method.addStatement("registerService.registerCreate($L, $T.valueOf(currentEntity.$L))", field.getFieldSnakeCase(), Util.class, field.getAccess());
        }

        method.endControlFlow();
        builder.addMethod(method.build());
    }

    private static MethodSpec.Builder buildMethodLogCreateUpdate(EntityMapping entity) {
        AnnotationSpec transactional = AnnotationSpec.builder(Transactional.class)
                .addMember("propagation", "$T.$L", Propagation.class, "REQUIRES_NEW")
                .addMember("isolation", "$T.$L", Isolation.class, "READ_COMMITTED")
                .build();

        return MethodSpec.methodBuilder("logCreateUpdate")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(transactional)
                .addParameter(entity.getEntityTypeName(), "currentEntity");
    }

    private static MethodSpec.Builder buildMethodLogDelete(EntityMapping entity) {
        return MethodSpec.methodBuilder("logDelete")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(entity.getId().asType()), "entityId");
    }
}
