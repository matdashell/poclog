package com.poczinha.log.processor.op;

import com.poczinha.log.hibernate.domain.SessionLogId;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.squareup.javapoet.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import javax.lang.model.element.Modifier;

public class CreateAspectOp {
    public void execute() {
        TypeSpec.Builder aspectJInterceptor = TypeSpec.classBuilder("LogAspect")
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                .addAnnotation(org.aspectj.lang.annotation.Aspect.class)
                .addAnnotation(org.springframework.stereotype.Component.class);

        FieldSpec.Builder sessionLogId = FieldSpec.builder(SessionLogId.class, "sessionLogId")
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(Autowired.class);

        for (EntityMapping entity : Context.mappings) {

            String methodName = "before" + entity.getEntityName();
            String beforeValue = "execution(* " + entity.getRepositoryPackage() + ".save(..))";
            String serviceName = entity.getEntityName() + "LogService";
            String serviceSimpleName = entity.getEntitySimpleName() + "LogService";

            ClassName service = ClassName.get(Context.packageName + ".log_entities", serviceName);

            FieldSpec.Builder entityService = FieldSpec.builder(service, serviceSimpleName)
                    .addModifiers(Modifier.PRIVATE)
                    .addAnnotation(Autowired.class);

            AnnotationSpec annotationSpec = AnnotationSpec.builder(Before.class)
                    .addMember("value", "$S", beforeValue)
                    .build();

            MethodSpec.Builder before = MethodSpec.methodBuilder(methodName)
                    .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                    .addAnnotation(annotationSpec)
                    .addParameter(JoinPoint.class, "jp")
                    .addStatement("$T entity = ($T) jp.getArgs()[0]", entity.asType(), entity.asType())
                    .addStatement("$L.logCreateUpdate(entity, sessionLogId.getId())", serviceSimpleName);

            aspectJInterceptor.addField(entityService.build());
            aspectJInterceptor.addMethod(before.build());
        }

        aspectJInterceptor.addField(sessionLogId.build());
        Processor.write(aspectJInterceptor.build());
    }
}
