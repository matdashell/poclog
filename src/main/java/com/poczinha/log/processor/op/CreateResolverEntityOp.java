package com.poczinha.log.processor.op;

import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.mapping.FieldMapping;
import com.poczinha.log.processor.util.Util;
import com.squareup.javapoet.*;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;

import javax.lang.model.element.Modifier;

import static com.poczinha.log.processor.Context.RESOLVER_NAME;

public class CreateResolverEntityOp {

    public void execute() {
        for (EntityMapping mapping : Context.mappings) {
            String className = mapping.getEntityName() + RESOLVER_NAME;

            TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC);

            createFields(mapping, builder);
            createConstructor(mapping, builder);
            createGetters(mapping, builder);

            Processor.write(builder.build(), Context.PACKAGE_RESOLVER_ENTITIES);
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
                .addParameter(SessionImplementor.class, "session")
                .addParameter(TypeName.get(mapping.asType()), "entity")
                .addModifiers(Modifier.PUBLIC);

        constructor.addStatement("$T entry = $T.getEntityEntry(session, entity)", EntityEntry.class, Util.class);
        constructor.addStatement("$T persistence = $T.getEntityPersister(session, entity)", EntityPersister.class, Util.class);

        constructor.addCode("\n");

        constructor.beginControlFlow("if (entry != null && persistence != null)");
        constructor.addStatement("$T[] loadedState = entry.getLoadedState()", Object.class);

        constructor.addCode("\n");

        for (FieldMapping field : mapping.getFields()) {
            constructor.addStatement("int $LIndex = $T.getIndexFromPropertyName(persistence, $S)", field.getFieldSimpleName(), Util.class, field.getFieldSimpleName());
        }

        constructor.addCode("\n");

        for (FieldMapping field : mapping.getFields()) {
            constructor.addStatement("this.$L = ($T) loadedState[$LIndex]", field.getFieldSimpleName(), ClassName.get(field.asType()), field.getFieldSimpleName());
        }

        constructor.addCode("\n");

        constructor.nextControlFlow("else");
        constructor.addStatement("throw new $T($S)", IllegalStateException.class, "Persistence context not found for entity " + mapping.getEntityName());
        constructor.endControlFlow();

        builder.addMethod(constructor.build());
    }
}
