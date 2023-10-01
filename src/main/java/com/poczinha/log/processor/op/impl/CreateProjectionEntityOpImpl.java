package com.poczinha.log.processor.op.impl;

import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.mapping.FieldMapping;
import com.poczinha.log.processor.op.CreateProjectionEntityOp;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

import static java.lang.String.format;

public class CreateProjectionEntityOpImpl implements CreateProjectionEntityOp {

    private String className;

    @Override
    public void execute() {
        for (EntityMapping mapping : Context.mappings) {
            this.className = mapping.getEntityName() + "Projection";

            TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC);

            String jpqlExpression = createjpqlExpression(mapping);
            FieldSpec fieldJpqlExpression = FieldSpec.builder(String.class, "PROJECTION_EXPRESSION")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", jpqlExpression)
                    .build();

            builder.addField(fieldJpqlExpression);
            createFields(mapping, builder);
            createConstructor(mapping, builder);
            createGetters(mapping, builder);

            Processor.write(builder.build(), Context.PACKAGE_PROJECTION_ENTITIES);
        }
    }

    private void createFields(EntityMapping mapping, TypeSpec.Builder builder) {
        for (FieldMapping field : mapping.getFields()) {
            FieldSpec fieldSpec = FieldSpec.builder(ClassName.get(field.asType()), field.getFieldSimpleName())
                    .addModifiers(Modifier.PRIVATE)
                    .build();

            builder.addField(fieldSpec);
        }
    }

    private void createGetters(EntityMapping mapping, TypeSpec.Builder builder) {
        for (FieldMapping field : mapping.getFields()) {
            MethodSpec.Builder method = MethodSpec.methodBuilder(field.getNameAccess())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ClassName.get(field.asType()))
                    .addStatement("return this.$L", field.getFieldSimpleName());

            builder.addMethod(method.build());
        }
    }

    private void createConstructor(EntityMapping mapping, TypeSpec.Builder builder) {
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        for (FieldMapping field : mapping.getFields()) {
            constructor.addParameter(ClassName.get(field.asType()), field.getFieldSimpleName());
            constructor.addStatement("this.$L = $L", field.getFieldSimpleName(), field.getFieldSimpleName());
        }

        builder.addMethod(constructor.build());
    }

    private String createjpqlExpression(EntityMapping mapping) {
        StringBuilder jpqlExpression = new StringBuilder("SELECT ");
        jpqlExpression.append("new ")
                .append(Context.packageName)
                .append(Context.PACKAGE_PROJECTION_ENTITIES)
                .append(".")
                .append(className)
                .append("(");

        for (int i = 0; i < mapping.getFields().size(); i++) {
            String fieldSimpleName = mapping.getFields().get(i).getFieldSimpleName();
            jpqlExpression.append("e.").append(fieldSimpleName);

            if (i < mapping.getFields().size() - 1) {
                jpqlExpression.append(", ");
            }
        }

        String name = mapping.getId().getName();
        jpqlExpression.append(") FROM ").append(mapping.getEntityName()).append(" e");
        jpqlExpression.append(format(" WHERE e.%s = :%s", name, name));

        return jpqlExpression.toString();
    }
}
