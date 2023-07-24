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

        MethodSpec.Builder equals = MethodSpec.methodBuilder("equals")
                .addModifiers(Modifier.PRIVATE)
                .returns(boolean.class)
                .addParameter(Object.class, "value1")
                .addParameter(Object.class, "value2")
                .beginControlFlow("if (value1 != null && value2 != null)")
                .addStatement("return value1.equals(value2)")
                .endControlFlow()
                .addStatement("return value1 == value2");

        builder.addMethod(valueOf.build());
        builder.addMethod(equals.build());

        for (EntityMapping entity : Context.mappings) {
            Element entityElement = entity.getEntity();
            MethodSpec.Builder method = MethodSpec.methodBuilder("log" + entityElement.getSimpleName())
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeName.get(entityElement.asType()), "curentEntity")
                    .addParameter(String.class, "identifier")
                    .addParameter(String.class, "operation");

            String idGetter = getGetter(entity.getId());

            method.addStatement("String tn = $S", getSimpleName(entityElement.getSimpleName()));
            method.beginControlFlow(format("if (curentEntity.%s() != null)", idGetter));
            method.addStatement(
                    "$T dbEntity = em.find($T.class, curentEntity.$L())",
                    TypeName.get(entityElement.asType()),
                    TypeName.get(entityElement.asType()),
                    idGetter);

            for (Element field : entity.getFields()) {
                String fieldGettter = getGetter(field.getSimpleName().toString());

                String filter;

                if (field.asType().getKind().isPrimitive()) {
                    filter = format("if (curentEntity.%s() != dbEntity.%s())", fieldGettter, fieldGettter);
                } else {
                    filter = format("if (equals(dbEntity.%s(), curentEntity.%s()))", fieldGettter, fieldGettter);
                }

                method.beginControlFlow(filter);

                String expression = format("registerService.register(tn, $S, identifier, operation, valueOf(dbEntity.%s()), valueOf(curentEntity.%s()))", fieldGettter, fieldGettter);
                method.addStatement(expression, getSimpleName(field.getSimpleName()));
                method.endControlFlow();
            }

            method.addStatement("em.close()");

            method.nextControlFlow("else");

            for (Element field : entity.getFields()) {
                String fieldGettter = getGetter(field.getSimpleName().toString());

                String expression = format("registerService.register(tn, $S, identifier, operation, null, valueOf(curentEntity.%s()))", fieldGettter);
                method.addStatement(expression, getSimpleName(field.getSimpleName()));
            }

            method.endControlFlow();

            builder.addMethod(method.build());
        }

        Processor.write(builder.build());
    }

    private String getGetter(String name) {
        return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private String getSimpleName(Name item) {
        String name = item.toString();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
