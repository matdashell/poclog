package com.poczinha.log.processor;

import com.google.auto.service.AutoService;
import com.poczinha.log.annotation.EnableLog;
import com.poczinha.log.annotation.LogEntity;
import com.poczinha.log.processor.processor.*;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Set;

import static com.poczinha.log.processor.util.Util.findCommonBasePackage;

@SupportedAnnotationTypes({
        "com.poczinha.log.annotation.LogEntity",
        "org.springframework.stereotype.Repository"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(javax.annotation.processing.Processor.class)
public class Processor extends AbstractProcessor {

    private final Logger log = LoggerFactory.getLogger(Processor.class);

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedWithLog = roundEnv.getElementsAnnotatedWith(LogEntity.class);
        Set<? extends Element> elementsAnnotatedWithEnableLog = roundEnv.getElementsAnnotatedWith(EnableLog.class);

        if (elementsAnnotatedWithLog.isEmpty()) return true;

        if (elementsAnnotatedWithEnableLog.isEmpty()) {
            log.warn("No @EnableLog annotation found on main class");
            return true;
        }

        log.info("Processing @EnableLog annotation");
        Element main = elementsAnnotatedWithEnableLog.iterator().next();
        setupContext(main, elementsAnnotatedWithLog, roundEnv);

        if (isDuplicatedProcessor()) return true;

        try {
            executeOperations();
        } catch (Exception e) {
            log.error("Error while processing: {}", (Object) e.getStackTrace());
            throw new RuntimeException(e);
        }

        return true;
    }

    private boolean isDuplicatedProcessor() {
        try {
            Class.forName(Context.packageName + Context.PACKAGE_CONFIGURATION + ".LogBeanConfiguration");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void setupContext(Element main, Set<? extends Element> elementsAnnotatedWithLog, RoundEnvironment roundEnv) {
        Context.filer = processingEnv.getFiler();
        Context.entities = elementsAnnotatedWithLog;
        Context.repositories = roundEnv.getElementsAnnotatedWith(Repository.class);
        Context.packageName = processingEnv.getElementUtils().getPackageOf(main).getQualifiedName().toString();

        Context.entitiesBasePackages = findCommonBasePackage(Context.entities);
        Context.repositoriesBasePackages = findCommonBasePackage(Context.repositories);
    }

    private void executeOperations() {
        log.debug("Executing entities collection");
        new CollectEntitiesProcessor().execute();

        log.debug("Executing resolver entities creation");
        new CreateResolverEntityProcessor().execute();

        log.debug("Executing aspect creation");
        new CreateAspectProcessor().execute();

        log.debug("Executing entities log services creation");
        new CreateEntitiesLogServicesProcessor().execute();

        log.debug("Executing configuration creation");
        new ConfigureProcessor().execute();

        log.info("Finished processing");
    }

    public static void write(TypeSpec execute, String packageName) {
        Logger log = LoggerFactory.getLogger(Processor.class);

        try {
            String createPackage = Context.packageName + packageName;
            JavaFile javaFile = JavaFile.builder(createPackage, execute).build();
            javaFile.writeTo(Context.filer);

            log.debug("File created: " + createPackage + "." + execute.name);
        } catch (IOException e) {
            log.error("Error while writing file: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
