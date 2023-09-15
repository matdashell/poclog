package com.poczinha.log.processor.op;

import com.poczinha.log.hibernate.entity.TableEntity;
import com.poczinha.log.hibernate.service.RegisterService;
import com.poczinha.log.hibernate.service.TableService;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.mapping.FieldMapping;
import com.poczinha.log.processor.util.Util;
import com.squareup.javapoet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.element.Modifier;
import javax.persistence.EntityManager;

import static com.poczinha.log.processor.util.Util.isTypeNumeric;
import static com.poczinha.log.processor.util.Util.log;
import static java.lang.String.format;

public class CreateEntitiesLogServicesOp {
    public void execute() {

        for (EntityMapping entity : Context.mappings) {

            String className = entity.getEntityName() + "LogService";

            TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Service.class);

            log("writing generateClassFields of " + className);
            generateClassFields(builder);

            log("writing createAndUpdateProcessor of " + className);
            createAndUpdateProcessor(builder, entity);

            log("writing deleteProcessor of " + className);
            deleteProcessor(builder, entity);

            log("writing class of " + className);
            Processor.write(builder.build());
        }
    }

    private void generateClassFields(TypeSpec.Builder builder) {

        FieldSpec.Builder registerService = FieldSpec.builder(RegisterService.class, "registerService")
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(Autowired.class);

        FieldSpec.Builder entityManager = FieldSpec.builder(EntityManager.class, "em")
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(Autowired.class);

        FieldSpec.Builder tableService = FieldSpec.builder(TableService.class, "tableService")
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(Autowired.class);

        builder.addField(registerService.build());
        builder.addField(entityManager.build());
        builder.addField(tableService.build());
    }

    private void deleteProcessor(TypeSpec.Builder builder, EntityMapping entity) {
        MethodSpec.Builder method = MethodSpec.methodBuilder("logDelete")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(entity.getId().asType()), "entityId")
                .addParameter(String.class, "identifier");

        method.addStatement("$T dbEntity = em.find($T.class, entityId)", entity.asType(), entity.asType());
        method.addStatement("$T tn = $S + \"[\" + entityId + \"]\"", String.class, entity.getName());
        method.addStatement("$T table = tableService.tableEntityWithName(tn)", TableEntity.class);

        method.addCode("\n");

        for (FieldMapping field : entity.getFields()) {
            method.addStatement("registerService.registerDelete(table, $S, identifier, $T.valueOf(dbEntity.$L))", field.getName(), Util.class, field.getAccess());
        }

        builder.addMethod(method.build());
    }

    private void createAndUpdateProcessor(TypeSpec.Builder builder, EntityMapping entity) {

        AnnotationSpec transactional = AnnotationSpec.builder(Transactional.class)
                .addMember("propagation", "$T.$L", Propagation.class, "REQUIRES_NEW")
                .addMember("isolation", "$T.$L", Isolation.class, "READ_COMMITTED")
                .build();

        MethodSpec.Builder method = MethodSpec.methodBuilder("logCreateUpdate")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(transactional)
                .addParameter(entity.getEntityTypeName(), "currentEntity")
                .addParameter(String.class, "identifier");

        FieldMapping id = entity.getId();

        method.addStatement("$T tn = $S", String.class, entity.getName());
        method.addStatement("$T table", TableEntity.class);
        method.addCode("\n");

        method.beginControlFlow(format("if (currentEntity.%s != null)", id.getAccess()));
        method.addStatement("$T dbEntity = em.find($T.class, currentEntity.$L)", entity.getEntityTypeName(), entity.getEntityTypeName(), id.getAccess());
        method.addStatement("tn = tn + \"[\" + dbEntity.$L + \"]\"", id.getAccess());
        method.addStatement("table = tableService.tableEntityWithName(tn)");

        method.addCode("\n");

        for (FieldMapping field : entity.getFields()) {

            if (field.asType().getKind().isPrimitive()) {
                method.beginControlFlow("if (currentEntity.$L != dbEntity.$L)", field.getAccess(), field.getAccess());

            } else if (isTypeNumeric(field.asType())) {
                method.beginControlFlow("if ($T.nuNotEquals(dbEntity.$L, currentEntity.$L))", Util.class, field.getAccess(), field.getAccess());

            } else {
                method.beginControlFlow("if ($T.obNotEquals(dbEntity.$L, currentEntity.$L))", Util.class, field.getAccess(), field.getAccess());
            }

            method.addStatement("registerService.registerUpdate(table, $S, identifier, $T.valueOf(dbEntity.$L), Util.valueOf(currentEntity.$L))", field.getName(), Util.class, field.getAccess(), field.getAccess());
            method.endControlFlow();
        }

        method.nextControlFlow("else");
        method.addStatement("table = tableService.tableEntityWithName(tn)");

        method.addCode("\n");

        for (FieldMapping field : entity.getFields()) {
            method.addStatement("registerService.registerCreate(table, $S, identifier, $T.valueOf(currentEntity.$L))", field.getName(), Util.class, field.getAccess());
        }

        method.endControlFlow();

        builder.addMethod(method.build());
    }
}
