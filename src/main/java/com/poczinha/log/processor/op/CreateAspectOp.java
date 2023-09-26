package com.poczinha.log.processor.op;

import com.poczinha.log.bean.manager.SessionIdentifier;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.util.Util;
import com.squareup.javapoet.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.lang.model.element.Modifier;
import java.util.List;

public class CreateAspectOp {
    public void execute() {
        String packageLogEntities = Context.packageName + ".log_entities." + Context.PACKAGE_LOG_ENTITIES;
        FieldSpec ignoreOnEmptyHeader = Util.buildFieldValue(Boolean.class, "ignoreOnEmptyHeader", "${audit.log.ignoreOnEmptyHeader:true}");
        FieldSpec sessionIdentifier = Util.buildFieldBean(SessionIdentifier.class, "sessionIdentifier");

        for (EntityMapping entity : Context.mappings) {

            ClassName service = ClassName.get(packageLogEntities, entity.getEntityName() + "LogService");
            String serviceSimpleName = entity.getEntitySimpleName() + "LogService";

            TypeSpec.Builder aspectJInterceptor = TypeSpec.classBuilder(entity.getEntityName() + "LogAspect")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Aspect.class)
                    .addAnnotation(Component.class);

            FieldSpec entityService = Util.buildFieldBean(service, serviceSimpleName);

            createSaveProcessor(entity, aspectJInterceptor, serviceSimpleName);
            createSaveAllProcessor(entity, aspectJInterceptor, serviceSimpleName);
            createDeleteProcessor(entity, aspectJInterceptor, serviceSimpleName);

            aspectJInterceptor.addField(ignoreOnEmptyHeader);
            aspectJInterceptor.addField(sessionIdentifier);
            aspectJInterceptor.addField(entityService);

            Processor.write(aspectJInterceptor.build(), Context.PACKAGE_ASPECT);
        }
    }

    private static void createSaveProcessor(EntityMapping entity, TypeSpec.Builder aspectJInterceptor, String serviceSimpleName) {
        String methodName = "beforeSave" + entity.getEntityName();
        String beforeValue = "execution(* " + entity.getRepositoryPackage() + ".save(..))";

        MethodSpec before = buildMethodBeforeSave(entity, serviceSimpleName, beforeValue, methodName);
        aspectJInterceptor.addMethod(before);
    }

    private static MethodSpec buildMethodBeforeSave(EntityMapping entity, String serviceSimpleName, String beforeValue, String methodName) {
        AnnotationSpec annotationSpec = AnnotationSpec.builder(Before.class)
                .addMember("value", "$S", beforeValue)
                .build();

        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(annotationSpec)
                .addParameter(JoinPoint.class, "jp")
                .beginControlFlow("if (sessionIdentifier.canLog(ignoreOnEmptyHeader))")
                .addStatement("$T entity = ($T) jp.getArgs()[0]", entity.asType(), entity.asType())
                .addStatement("$L.logCreateUpdate(entity, sessionIdentifier.getIdentifier())", serviceSimpleName)
                .endControlFlow()
                .build();
    }

    private void createSaveAllProcessor(EntityMapping entity, TypeSpec.Builder aspectJInterceptor, String serviceSimpleName) {
        String methodName = "beforeSaveAll" + entity.getEntityName();
        String beforeValue = "execution(* " + entity.getRepositoryPackage() + ".saveAll(..))";

        MethodSpec before = buildMethodBeforeSaveAll(entity, serviceSimpleName, beforeValue, methodName);

        aspectJInterceptor.addMethod(before);
    }

    private static MethodSpec buildMethodBeforeSaveAll(EntityMapping entity, String serviceSimpleName, String beforeValue, String methodName) {
        AnnotationSpec annotationSpec = AnnotationSpec.builder(Before.class)
                .addMember("value", "$S", beforeValue)
                .build();

        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(annotationSpec)
                .addParameter(JoinPoint.class, "jp")
                .beginControlFlow("if (sessionIdentifier.canLog(ignoreOnEmptyHeader))")
                .addStatement("$T<$T> entities = ($T) jp.getArgs()[0]", List.class, entity.asType(), List.class)
                .beginControlFlow("for ($T entity : entities)", entity.asType())
                .addStatement("$L.logCreateUpdate(entity, sessionIdentifier.getIdentifier())", serviceSimpleName)
                .endControlFlow()
                .endControlFlow()
                .build();
    }

    private void createDeleteProcessor(EntityMapping entity, TypeSpec.Builder aspectJInterceptor, String serviceSimpleName) {
        String methodName = "beforeDelete" + entity.getEntityName();
        String beforeValue = "execution(* " + entity.getRepositoryPackage() + ".deleteById(..))";

        MethodSpec before = buildMethodBeforeDelete(entity, serviceSimpleName, beforeValue, methodName);

        aspectJInterceptor.addMethod(before);
    }

    private static MethodSpec buildMethodBeforeDelete(EntityMapping entity, String serviceSimpleName, String beforeValue, String methodName) {
        AnnotationSpec annotationSpec = AnnotationSpec.builder(Before.class)
                .addMember("value", "$S", beforeValue)
                .build();

        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(annotationSpec)
                .addParameter(JoinPoint.class, "jp")
                .beginControlFlow("if (sessionIdentifier.canLog(ignoreOnEmptyHeader))")
                .addStatement("$T entityId = ($T) jp.getArgs()[0]", entity.getId().asType(), entity.getId().asType())
                .addStatement("$L.logDelete(entityId, sessionIdentifier.getIdentifier())", serviceSimpleName)
                .endControlFlow()
                .build();
    }
}
