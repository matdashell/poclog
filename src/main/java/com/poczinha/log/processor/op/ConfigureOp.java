package com.poczinha.log.processor.op;

import com.poczinha.log.bean.manager.SessionIdentifier;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.Processor;
import com.poczinha.log.processor.util.Util;
import com.poczinha.log.service.KafkaProducerService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.lang.model.element.Modifier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfigureOp {

    public void execute() {
        createIntercepto();
    }

    private void createIntercepto() {
        TypeSpec.Builder headerInterceptor = TypeSpec.classBuilder("LogHeaderInterceptor")
                .addAnnotation(Configuration.class)
                .addSuperinterface(WebMvcConfigurer.class);

        MethodSpec overrideInterface = buildMethodAddInterceptors();

        FieldSpec logHeaderNameField = Util.buildFieldValue(String.class, "logHeaderName", "${audit.log.headerName:X-log-id}");
        FieldSpec sessionIdentifier = Util.buildFieldBean(SessionIdentifier.class, "sessionIdentifier");
        FieldSpec kafkaProducerService = Util.buildFieldBean(KafkaProducerService.class, "kafkaProducerService");

        headerInterceptor.addMethod(overrideInterface);

        headerInterceptor.addField(logHeaderNameField);
        headerInterceptor.addField(sessionIdentifier);
        headerInterceptor.addField(kafkaProducerService);

        Processor.write(headerInterceptor.build(), Context.PACKAGE_CONFIGURATION);
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
                .addCode("\n")
                .addCode("registry.addInterceptor(new $T() {\n", HandlerInterceptor.class)
                .addCode("  @Override\n")
                .addCode("  public void afterCompletion($T request, $T response, $T handler, $T ex) {\n", HttpServletRequest.class, HttpServletResponse.class, Object.class, Exception.class)
                .addCode("    if (sessionIdentifier.containsData()) {\n")
                .addCode("      kafkaProducerService.sendAuditMessage(sessionIdentifier.getCorrelationModification());\n")
                .addCode("    }\n")
                .addCode("  }\n")
                .addCode("});\n")
                .build();
    }
}
