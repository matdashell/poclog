package com.poczinha.log.processor.op;

import com.poczinha.log.hibernate.service.RegisterService;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.squareup.javapoet.*;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;

import java.time.LocalDateTime;

import static java.lang.String.format;

public class CreateEntitiesLogServicesOp {
    public void execute() {

        TypeSpec.Builder builder = TypeSpec.classBuilder("LogService")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Service.class);

        FieldSpec registerService = FieldSpec.builder(RegisterService.class, "registerService")
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(Autowired.class)
                .build();

        FieldSpec entityManager = FieldSpec.builder(EntityManager.class, "em")
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(Autowired.class)
                .build();

        builder.addField(registerService);
        builder.addField(entityManager);

        MethodSpec.Builder valueOf = MethodSpec.methodBuilder("valueOf")
                .addModifiers(Modifier.PRIVATE)
                .returns(String.class)
                .addParameter(Object.class, "value")
                .beginControlFlow("if (value != null)")
                .addStatement("return String.valueOf(value)")
                .endControlFlow()
                .addStatement("return null");

        MethodSpec.Builder notEquals = MethodSpec.methodBuilder("notEquals")
                .addModifiers(Modifier.PRIVATE)
                .returns(boolean.class)
                .addParameter(Object.class, "value1")
                .addParameter(Object.class, "value2")
                .beginControlFlow("if (value1 != null && value2 != null)")
                .addStatement("return !value1.equals(value2)")
                .endControlFlow()
                .addStatement("return value1 != value2");

        builder.addMethod(valueOf.build());
        builder.addMethod(notEquals.build());

        System.out.println("writing createAndUpdateProcessor");
        createAndUpdateProcessor(builder);

        System.out.println("writing deleteProcessor");
        deleteProcessor(builder);

        System.out.println("writing LogService");
        Processor.write(builder.build());
    }

    private String getGetter(String name) {
        return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private String getSimpleName(Name item) {
        String name = item.toString();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private void deleteProcessor(TypeSpec.Builder builder) {
        for (EntityMapping entity : Context.mappings) {
            Element entityElement = entity.getEntity();

            MethodSpec.Builder method = MethodSpec.methodBuilder("logDelete" + entityElement.getSimpleName())
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeName.get(entity.getIdType()), "entityId")
                    .addParameter(String.class, "identifier");

            method.addStatement(
                    "$T dbEntity = em.find($T.class, entityId)",
                    TypeName.get(entityElement.asType()),
                    TypeName.get(entityElement.asType()));

            method.addStatement("$T now = LocalDateTime.now()", TypeName.get(LocalDateTime.class));
            method.addStatement("String tn = $S + \"[\" + entityId + \"]\"", getSimpleName(entityElement.getSimpleName()));
            method.addCode("\n");

            for (Element field : entity.getFields()) {
                String fieldGettter = getGetter(field.getSimpleName().toString());

                String expression = format("registerService.registerDelete(tn, $S, identifier, valueOf(dbEntity.%s()), now)", fieldGettter);
                method.addStatement(expression, getSimpleName(field.getSimpleName()));
            }

            method.addStatement("em.close()");
            builder.addMethod(method.build());
        }
    }

    private void createAndUpdateProcessor(TypeSpec.Builder builder) {
        for (EntityMapping entity : Context.mappings) {
            Element entityElement = entity.getEntity();
            MethodSpec.Builder method = MethodSpec.methodBuilder("log" + entityElement.getSimpleName())
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeName.get(entityElement.asType()), "currentEntity")
                    .addParameter(String.class, "identifier");

            String idGetter = getGetter(entity.getId());

            method.addStatement("$T now = LocalDateTime.now()", TypeName.get(LocalDateTime.class));
            method.addStatement("String tn = $S", getSimpleName(entityElement.getSimpleName()));
            method.addCode("\n");

            method.beginControlFlow(format("if (currentEntity.%s() != null)", idGetter));
            method.addStatement(
                    "$T dbEntity = em.find($T.class, currentEntity.$L())",
                    TypeName.get(entityElement.asType()),
                    TypeName.get(entityElement.asType()),
                    idGetter);

            method.addStatement(format("tn = tn + \"[\" + dbEntity.%s() + \"]\"", idGetter));
            method.addCode("\n");

            for (Element field : entity.getFields()) {
                String fieldGettter = getGetter(field.getSimpleName().toString());

                String filter;

                if (field.asType().getKind().isPrimitive()) {
                    filter = format("if (currentEntity.%s() != dbEntity.%s())", fieldGettter, fieldGettter);
                } else {
                    filter = format("if (notEquals(dbEntity.%s(), currentEntity.%s()))", fieldGettter, fieldGettter);
                }

                method.beginControlFlow(filter);

                String expression = format("registerService.registerUpdate(tn, $S, identifier, valueOf(dbEntity.%s()), valueOf(currentEntity.%s()), now)", fieldGettter, fieldGettter);
                method.addStatement(expression, getSimpleName(field.getSimpleName()));
                method.endControlFlow();
            }

            method.addStatement("em.close()");

            method.nextControlFlow("else");

            for (Element field : entity.getFields()) {
                String fieldGettter = getGetter(field.getSimpleName().toString());

                String expression = format("registerService.registerCreate(tn, $S, identifier, valueOf(currentEntity.%s()), now)", fieldGettter);
                method.addStatement(expression, getSimpleName(field.getSimpleName()));
            }

            method.endControlFlow();

            builder.addMethod(method.build());
        }
    }
}
