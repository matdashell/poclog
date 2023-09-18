package com.poczinha.log.processor;

import com.google.auto.service.AutoService;
import com.poczinha.log.annotation.EnableLog;
import com.poczinha.log.annotation.LogPersistenceEntities;
import com.poczinha.log.processor.util.PrefixLogger;
import com.poczinha.log.processor.util.Util;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.springframework.stereotype.Repository;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.persistence.Entity;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes({"com.poczinha.log.annotation.*", "jakarta.persistence.Entity"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(javax.annotation.processing.Processor.class)
public class Processor extends AbstractProcessor {

    private final PrefixLogger log = new PrefixLogger(Processor.class);

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedWithLog = roundEnv.getElementsAnnotatedWith(LogPersistenceEntities.class);
        Set<? extends Element> elementsAnnotatedWithEnableLog = roundEnv.getElementsAnnotatedWith(EnableLog.class);

        if (elementsAnnotatedWithLog.isEmpty()) return true;

        if (elementsAnnotatedWithEnableLog.isEmpty()) {
            log.info("No @EnableLog annotation found on main class");
            return true;
        }

        log.info("Processing @EnableLog annotation");
        Element main = elementsAnnotatedWithEnableLog.iterator().next();
        setupContext(main, elementsAnnotatedWithLog, roundEnv);

        try {
            executeOperations();
        } catch (Exception e) {
            log.error("Error while processing: {}", e, e.getMessage());
        }

        return true;
    }

    private void setupContext(Element main, Set<? extends Element> elementsAnnotatedWithLog, RoundEnvironment roundEnv) {
        EnableLog annotation = main.getAnnotation(EnableLog.class);
        Context.filer = processingEnv.getFiler();
        Context.repositories = elementsAnnotatedWithLog;
        Context.packageName = processingEnv.getElementUtils().getPackageOf(main).getQualifiedName().toString();

        Util.setBasePackages(
                roundEnv.getElementsAnnotatedWith(Entity.class),
                roundEnv.getElementsAnnotatedWith(Repository.class)
        );

        log.info("Context: {}", Context.logInfos());
    }

    private void executeOperations() {
        log.debug("Executing operations");
        Context.collectEntitiesOp.execute();

        log.debug("Executing operations");
        Context.createAspectOp.execute();

        log.debug("Executing operations");
        Context.createEntitiesLogServicesOp.execute();

        log.debug("Executing operations");
        Context.configureOp.execute();

        log.info("Finished processing");
    }

    public static void write(TypeSpec execute, String packageName) {
        PrefixLogger log = new PrefixLogger(Processor.class);
        try {
            JavaFile javaFile = JavaFile.builder(Context.packageName + ".log_entities." + packageName, execute).build();
            javaFile.writeTo(Context.filer);
        } catch (IOException e) {
            log.error("Error while writing file: {}", e, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
