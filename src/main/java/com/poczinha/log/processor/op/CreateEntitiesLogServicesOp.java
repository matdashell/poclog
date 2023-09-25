package com.poczinha.log.processor.op;

import com.poczinha.log.bean.SessionIdentifier;
import com.poczinha.log.bean.TypeCountManager;
import com.poczinha.log.domain.response.data.FieldModification;
import com.poczinha.log.domain.response.data.GroupTypeModifications;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.mapping.FieldMapping;
import com.poczinha.log.processor.util.Util;
import com.squareup.javapoet.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static com.poczinha.log.processor.util.Util.isTypeNumeric;

public class CreateEntitiesLogServicesOp {
    public void execute() {

        for (EntityMapping entity : Context.mappings) {

            String className = entity.getEntityName() + "LogService";

            TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Service.class);

            generateClassFields(builder);

            createAndUpdateProcessor(builder, entity);

            deleteProcessor(builder, entity);

            Processor.write(builder.build(), Context.PACKAGE_LOG_ENTITIES);
        }
    }

    private void generateClassFields(TypeSpec.Builder builder) {

        FieldSpec typCountManager = Util.buildFieldBean(TypeCountManager.class, "typeCountManager");
        FieldSpec sessionIdentifier = Util.buildFieldBean(SessionIdentifier.class, "sessionIdentifier");
        FieldSpec entityManager = Util.buildFieldBean(EntityManager.class, "em");

        builder.addField(typCountManager);
        builder.addField(sessionIdentifier);
        builder.addField(entityManager);
    }

    private void deleteProcessor(TypeSpec.Builder builder, EntityMapping entity) {
        MethodSpec.Builder method = buildMethodLogDelete(entity);
        TypeMirror typeEtity = entity.asType();

        method.addCode("\n");
        method.addStatement("$T groupTypeMod = new $T()", GroupTypeModifications.class, GroupTypeModifications.class);
        method.addStatement("$T<$T> fieldModList = new $T<>()", List.class, FieldModification.class, ArrayList.class);
        method.addCode("\n");

        method.addStatement("$T dbEntity = em.find($T.class, entityId)", typeEtity, typeEtity);
        method.addCode("\n");

        for (FieldMapping field : entity.getFields()) {
            method.addStatement("fieldModList.add(new $T($S, null, $T.valueOf(dbEntity.$L)))", FieldModification.class, field.getName(), Util.class, field.getAccess());
        }

        method.addCode("\n");
        method.beginControlFlow("if (!fieldModList.isEmpty())");
        method.addStatement("groupTypeMod.setType(typeCountManager.getDeletion())");
        method.addStatement("groupTypeMod.addModifications(fieldModList)");
        method.addStatement("typeCountManager.countDeletion()");
        method.addStatement("sessionIdentifier.addGroup(groupTypeMod)");
        method.endControlFlow();

        builder.addMethod(method.build());
    }

    private void createAndUpdateProcessor(TypeSpec.Builder builder, EntityMapping entity) {

        MethodSpec.Builder method = buildMethodLogCreateUpdate(entity);
        FieldMapping id = entity.getId();

        TypeName entityTypeName = entity.getEntityTypeName();
        String access = id.getAccess();

        method.addCode("\n");
        method.addStatement("$T groupTypeMod = new $T()", GroupTypeModifications.class, GroupTypeModifications.class);
        method.addStatement("$T<$T> fieldModList = new $T<>()", List.class, FieldModification.class, ArrayList.class);

        method.addCode("\n");
        method.beginControlFlow("if (currentEntity.$L != null)", access);
        method.addStatement("$T dbEntity = em.find($T.class, currentEntity.$L)", entityTypeName, entityTypeName, access);
        method.addCode("\n");

        for (FieldMapping field : entity.getFields()) {
            String fieldAccess = field.getAccess();

            if (field.asType().getKind().isPrimitive()) {
                method.beginControlFlow("if (currentEntity.$L != dbEntity.$L)", fieldAccess, fieldAccess);

            } else if (isTypeNumeric(field.asType())) {
                method.beginControlFlow("if ($T.nuNotEquals(dbEntity.$L, currentEntity.$L))", Util.class, fieldAccess, fieldAccess);

            } else {
                method.beginControlFlow("if ($T.obNotEquals(dbEntity.$L, currentEntity.$L))", Util.class, fieldAccess, fieldAccess);
            }

            method.addStatement("fieldModList.add(new $T($S, $T.valueOf(currentEntity.$L), $T.valueOf(dbEntity.$L)))", FieldModification.class, field.getName(), Util.class, fieldAccess, Util.class, fieldAccess);
            method.endControlFlow();
        }

        method.addCode("\n");
        method.beginControlFlow("if (!fieldModList.isEmpty())");
        method.addStatement("groupTypeMod.setType(typeCountManager.getModification())");
        method.addStatement("typeCountManager.countModification()");
        method.endControlFlow();
        method.addCode("\n");

        method.nextControlFlow("else");
        method.addCode("\n");

        for (FieldMapping field : entity.getFields()) {
            method.addStatement("fieldModList.add(new $T($S, $T.valueOf(currentEntity.$L), null))", FieldModification.class, field.getName(), Util.class, field.getAccess());
        }

        method.addCode("\n");
        method.beginControlFlow("if (!fieldModList.isEmpty())");
        method.addStatement("groupTypeMod.setType(typeCountManager.getCreation())");
        method.addStatement("typeCountManager.countCreation()");
        method.endControlFlow();

        method.endControlFlow();

        method.addCode("\n");
        method.beginControlFlow("if (!fieldModList.isEmpty())");
        method.addStatement("groupTypeMod.addModifications(fieldModList)");
        method.addStatement("sessionIdentifier.addGroup(groupTypeMod)");
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
                .addParameter(entity.getEntityTypeName(), "currentEntity")
                .addParameter(String.class, "identifier");
    }

    private static MethodSpec.Builder buildMethodLogDelete(EntityMapping entity) {
        return MethodSpec.methodBuilder("logDelete")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(entity.getId().asType()), "entityId")
                .addParameter(String.class, "identifier");
    }
}
