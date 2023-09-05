package com.poczinha.log.processor.op;

import com.poczinha.log.hibernate.controler.LogController;
import com.poczinha.log.hibernate.domain.Correlation;
import com.poczinha.log.hibernate.service.RegisterService;
import com.poczinha.log.processor.Processor;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import javax.lang.model.element.Modifier;

public class ConfigureOp {

    public void execute() {

        AnnotationSpec scopeRequest = AnnotationSpec.builder(Scope.class)
                .addMember("value", "$T.$L", WebApplicationContext.class, "SCOPE_REQUEST")
                .addMember("proxyMode", "$T.$L", ScopedProxyMode.class, "TARGET_CLASS")
                .build();

        TypeSpec.Builder builder = TypeSpec.classBuilder("LogConfiguration")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Configuration.class);

        MethodSpec.Builder correlation = MethodSpec.methodBuilder("correlation")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Bean.class)
                .addAnnotation(scopeRequest)
                .returns(Correlation.class)
                .addStatement("return new Correlation()");

        MethodSpec.Builder registerService = MethodSpec.methodBuilder("registerService")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Bean.class)
                .returns(RegisterService.class)
                .addStatement("return new RegisterService()");

        MethodSpec.Builder loggerController = MethodSpec.methodBuilder("LogController")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Bean.class)
                .returns(LogController.class)
                .addStatement("return new LogController()");

        builder.addMethod(correlation.build());
        builder.addMethod(registerService.build());
        builder.addMethod(loggerController.build());

        Processor.write(builder.build());
    }
}
