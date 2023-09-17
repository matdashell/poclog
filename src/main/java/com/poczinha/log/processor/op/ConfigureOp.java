package com.poczinha.log.processor.op;

import com.poczinha.log.bean.LogHeaderConfiguration;
import com.poczinha.log.domain.Constants;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.context.WebApplicationContext;
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

        FieldSpec logHeaderConfiguration = buildFieldLogHeaderConfiguration();
        MethodSpec overrideInterface = buildMethodAddInterceptors();

        headerInterceptor.addField(logHeaderConfiguration);
        headerInterceptor.addMethod(overrideInterface);
        Processor.write(headerInterceptor.build(), Context.PACKAGE_CONFIGURATION);
    }

    private void createConfiguration() {

        TypeSpec.Builder configuration = TypeSpec.classBuilder("LogBeanConfiguration")
                .addAnnotation(Configuration.class);

        AnnotationSpec entityScan = buildAnnotationEntityScan();
        AnnotationSpec repositoryScan = buildAnnotationEnableJpaRepositories();
        MethodSpec bean = buildBeanLogHeaderConfiguration();

        configuration.addAnnotation(entityScan);
        configuration.addAnnotation(repositoryScan);
        configuration.addMethod(bean);

        Processor.write(configuration.build(), Context.PACKAGE_CONFIGURATION);
    }

    private static MethodSpec buildBeanLogHeaderConfiguration() {
        AnnotationSpec scope = AnnotationSpec.builder(Scope.class)
                .addMember("value", "$T.$L", WebApplicationContext.class, "SCOPE_REQUEST")
                .addMember("proxyMode", "$T.$L", ScopedProxyMode.class, "TARGET_CLASS")
                .build();

        return MethodSpec.methodBuilder("logHeaderConfiguration")
                .addAnnotation(Bean.class)
                .addAnnotation(scope)
                .addModifiers(Modifier.PUBLIC)
                .returns(LogHeaderConfiguration.class)
                .addStatement("return new $T($L)", LogHeaderConfiguration.class, Context.logOnlyIfPresent)
                .build();
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
                .addCode("    if (request.getHeader($S) != null) {\n", Context.idName)
                .addCode("      logHeaderConfiguration.setId(request.getHeader($S));\n", Context.idName)
                .addCode("    }\n")
                .addCode("    return true;\n")
                .addCode("  }\n")
                .addCode("});\n")
                .build();
    }

    private static FieldSpec buildFieldLogHeaderConfiguration() {
        return FieldSpec.builder(LogHeaderConfiguration.class, "logHeaderConfiguration")
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(Autowired.class)
                .build();
    }
}
