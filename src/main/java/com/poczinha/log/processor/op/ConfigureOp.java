package com.poczinha.log.processor.op;

import com.poczinha.log.bean.SessionIdentifier;
import com.poczinha.log.domain.Constants;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.poczinha.log.processor.util.Util;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.lang.model.element.Modifier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfigureOp {

    public void execute() {
        createIntercepto();
        createConfiguration();
    }

    private void createIntercepto() {
        TypeSpec.Builder headerInterceptor = TypeSpec.classBuilder("LogHeaderInterceptor")
                .addAnnotation(Configuration.class)
                .addSuperinterface(WebMvcConfigurer.class);

        MethodSpec overrideInterface = buildMethodAddInterceptors();
        FieldSpec logHeaderNameField = Util.buildFieldValue(String.class, "logHeaderName", "${audit.log.headerName:X-log-id}");
        FieldSpec sessionIdentifier = Util.buildFieldBean(SessionIdentifier.class, "sessionIdentifier");

        headerInterceptor.addMethod(overrideInterface);
        headerInterceptor.addField(logHeaderNameField);
        headerInterceptor.addField(sessionIdentifier);

        Processor.write(headerInterceptor.build(), Context.PACKAGE_CONFIGURATION);
    }

    private void createConfiguration() {

        TypeSpec.Builder configuration = TypeSpec.classBuilder("LogBeanConfiguration")
                .addAnnotation(Configuration.class);

        AnnotationSpec entityScan = buildAnnotationEntityScan();
        AnnotationSpec repositoryScan = buildAnnotationEnableJpaRepositories();

        configuration.addAnnotation(entityScan);
        configuration.addAnnotation(repositoryScan);

        Processor.write(configuration.build(), Context.PACKAGE_CONFIGURATION);
    }

    private static AnnotationSpec buildAnnotationEnableJpaRepositories() {
        return AnnotationSpec.builder(EnableJpaRepositories.class)
                .addMember("basePackages", "{$S, $S}", Context.repositoriesBasePackages, Constants.LOG_REPOSITORY_SCAN)
                .build();
    }

    private static AnnotationSpec buildAnnotationEntityScan() {
        return AnnotationSpec.builder(EntityScan.class)
                .addMember("basePackages", "{$S, $S}", Context.entitiesBasePackages, Constants.LOG_ENTITY_SCAN)
                .build();
    }

    private static MethodSpec buildMethodAddInterceptors() {
        return MethodSpec.methodBuilder("addInterceptors")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(InterceptorRegistry.class, "registry")
                .addCode("registry.addInterceptor(new $T() {\n", HandlerInterceptor.class)
                .addCode("  @Override\n")
                .addCode("  public boolean preHandle($T request, $T response, $T handler) {\n", HttpServletRequest.class, HttpServletResponse.class, Object.class)
                .addCode("    if (request.getHeader(logHeaderName) != null) {\n")
                .addCode("      sessionIdentifier.setIdentifier(request.getHeader(logHeaderName));\n")
                .addCode("    }\n")
                .addCode("    return true;\n")
                .addCode("  }\n")
                .addCode("});\n")
                .build();
    }
}
