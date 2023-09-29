package com.poczinha.log.processor.op.impl;

import com.poczinha.log.bean.Correlation;
import com.poczinha.log.bean.RegisterManager;
import com.poczinha.log.hibernate.entity.RegisterEntity;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.op.CreateAspectOp;
import com.poczinha.log.processor.util.Util;
import com.squareup.javapoet.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.lang.model.element.Modifier;
import java.util.List;

public class CreateAspectOpImpl implements CreateAspectOp {

    @Override
    public void execute() {
        String packageLogEntities = Context.packageName + ".log_entities." + Context.PACKAGE_LOG_ENTITIES;
        FieldSpec ignoreOnEmptyHeader = Util.buildFieldValue(Boolean.class, "ignoreOnEmptyHeader", "${audit.log.ignoreOnEmptyHeader:true}");
        FieldSpec correlation = Util.buildFieldBean(Correlation.class, "correlation");
        FieldSpec registerManager = Util.buildFieldBean(RegisterManager.class, "registerManager");

        for (EntityMapping entity : Context.mappings) {

            ClassName service = ClassName.get(packageLogEntities, entity.getEntityName() + "LogService");
            String serviceSimpleName = entity.getEntitySimpleName() + "LogService";

            TypeSpec.Builder aspectJInterceptor = TypeSpec.classBuilder(entity.getEntityName() + "LogAspect")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Aspect.class)
                    .addAnnotation(Component.class);

            FieldSpec entityService = Util.buildFieldBean(service, serviceSimpleName);

            createSaveProcessor(entity, aspectJInterceptor, serviceSimpleName);
            createDeleteProcessor(entity, aspectJInterceptor, serviceSimpleName);

            aspectJInterceptor.addField(ignoreOnEmptyHeader);
            aspectJInterceptor.addField(correlation);
            aspectJInterceptor.addField(entityService);
            aspectJInterceptor.addField(registerManager);

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
                .beginControlFlow("if (correlation.canLog(ignoreOnEmptyHeader))")
                    .addStatement("$T entity = ($T) jp.getArgs()[0]", entity.asType(), entity.asType())
                    .addStatement("$T<$T> regs = $L.processLogCreateUpdate(entity)", List.class, RegisterEntity.class, serviceSimpleName)
                    .beginControlFlow("try")
                        .addStatement("$T result = jp.proceed()", Object.class)
                        .addStatement("registerManager.addRegisterEntities(regs, $T.valueOf(entity.$L))", String.class, entity.getId().getAccess())
                        .addStatement("return result")
                        .nextControlFlow("catch ($T e)", Throwable.class)
                        .addStatement("registerManager.rollback()")
                        .addStatement("throw e")
                    .endControlFlow()
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
                    .beginControlFlow("if (correlation.canLog(ignoreOnEmptyHeader))")
                    .addStatement("$T entityId = ($T) jp.getArgs()[0]", entity.getId().asType(), entity.getId().asType())
                    .addStatement("$T<$T> regs = $L.processLogDelete(entityId)", List.class, RegisterEntity.class, serviceSimpleName)
                    .beginControlFlow("try")
                        .addStatement("$T result = jp.proceed()", Object.class)
                        .addStatement("registerManager.addRegisterEntities(regs, $T.valueOf(entityId))", String.class)
                        .addStatement("return result")
                        .nextControlFlow("catch ($T e)", Throwable.class)
                        .addStatement("registerManager.rollback()")
                        .addStatement("throw e")
                    .endControlFlow()
                .nextControlFlow("else")
                    .addStatement("return jp.proceed()")
                .endControlFlow()
                .build();
    }
}
