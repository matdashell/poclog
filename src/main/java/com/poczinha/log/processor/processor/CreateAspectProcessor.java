package com.poczinha.log.processor.processor;

import com.poczinha.log.bean.LogSessionRegisterManager;
import com.poczinha.log.hibernate.entity.LogRegisterEntity;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.util.Util;
import com.squareup.javapoet.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.lang.model.element.Modifier;
import java.util.List;

import static com.poczinha.log.processor.Context.ASPECT_NAME;
import static com.poczinha.log.processor.Context.SERVICE_NAME;

public class CreateAspectProcessor {

    public void execute() {
        String packageLogEntities = Context.packageName + Context.PACKAGE_LOG_ENTITIES;
        FieldSpec logSessionRegisterManager = Util.buildFieldBean(LogSessionRegisterManager.class, "logSessionRegisterManager");

        for (EntityMapping entity : Context.mappings) {

            String className = entity.getEntityName();
            ClassName service = ClassName.get(packageLogEntities, className + SERVICE_NAME);
            String serviceSimpleName = entity.getEntitySimpleName() + SERVICE_NAME;

            TypeSpec.Builder aspectJInterceptor = TypeSpec.classBuilder(className + ASPECT_NAME)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Aspect.class)
                    .addAnnotation(Component.class)
                    .addAnnotation(Util.getGeneratedAnnotation("Listeners of repository for entity " + className));

            FieldSpec entityService = Util.buildFieldBean(service, serviceSimpleName);

            createSaveProcessor(entity, aspectJInterceptor, serviceSimpleName);
            createDeleteProcessor(entity, aspectJInterceptor, serviceSimpleName);

            aspectJInterceptor.addField(entityService);
            aspectJInterceptor.addField(logSessionRegisterManager);

            Processor.write(aspectJInterceptor.build(), Context.PACKAGE_ASPECT);
        }
    }

    private void createSaveProcessor(EntityMapping entity, TypeSpec.Builder aspectJInterceptor, String serviceSimpleName) {
        String methodName = "beforeSave" + entity.getEntityName();
        String beforeValue = "execution(* " + entity.getRepositoryPackage() + ".save(..))";

        MethodSpec before = buildMethodBeforeSave(entity, serviceSimpleName, beforeValue, methodName);
        aspectJInterceptor.addMethod(before);
    }

    private MethodSpec buildMethodBeforeSave(EntityMapping entity, String serviceSimpleName, String beforeValue, String methodName) {
        AnnotationSpec annotationSpec = AnnotationSpec.builder(Around.class)
                .addMember("value", "$S", beforeValue)
                .build();

        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(annotationSpec)
                .addException(Throwable.class)
                .returns(Object.class)
                .addParameter(ProceedingJoinPoint.class, "jp")
                .beginControlFlow("if (logSessionRegisterManager.canLog())")
                    .addStatement("$T entity = ($T) jp.getArgs()[0]", entity.asType(), entity.asType())
                    .addStatement("$T<$T> registers = $L.processLogCreateUpdate(entity)", List.class, LogRegisterEntity.class, serviceSimpleName)
                    .addStatement("$T result = logSessionRegisterManager.execute(jp)", Object.class)
                    .addStatement("logSessionRegisterManager.addRegisterEntities(registers, entity.$L)", entity.getId().getAccess())
                    .addStatement("return result")
                .nextControlFlow("else")
                    .addStatement("return jp.proceed()")
                .endControlFlow()
                .build();
    }

    private void createDeleteProcessor(EntityMapping entity, TypeSpec.Builder aspectJInterceptor, String serviceSimpleName) {
        String methodName = "beforeDelete" + entity.getEntityName();
        String beforeValue = "execution(* " + entity.getRepositoryPackage() + ".deleteById(..))";

        MethodSpec before = buildMethodBeforeDelete(entity, serviceSimpleName, beforeValue, methodName);

        aspectJInterceptor.addMethod(before);
    }

    private MethodSpec buildMethodBeforeDelete(EntityMapping entity, String serviceSimpleName, String beforeValue, String methodName) {
        AnnotationSpec annotationSpec = AnnotationSpec.builder(Around.class)
                .addMember("value", "$S", beforeValue)
                .build();

        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(annotationSpec)
                .addException(Throwable.class)
                .returns(Object.class)
                .addParameter(ProceedingJoinPoint.class, "jp")
                    .beginControlFlow("if (logSessionRegisterManager.canLog())")
                    .addStatement("$T entityId = ($T) jp.getArgs()[0]", entity.getId().asType(), entity.getId().asType())
                    .addStatement("$T<$T> registers = $L.processLogDelete()", List.class, LogRegisterEntity.class, serviceSimpleName)
                    .addStatement("return logSessionRegisterManager.executeAndRegister(jp, registers, entityId)")
                .nextControlFlow("else")
                    .addStatement("return jp.proceed()")
                .endControlFlow()
                .build();
    }
}
