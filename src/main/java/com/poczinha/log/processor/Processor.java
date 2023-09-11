package com.poczinha.log.processor;

import com.google.auto.service.AutoService;
import com.poczinha.log.processor.annotation.EnableLog;
import com.poczinha.log.processor.annotation.LogEntity;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Set;

import static com.poczinha.log.processor.util.Util.log;

@SupportedAnnotationTypes({"com.poczinha.log.processor.annotation.*"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(javax.annotation.processing.Processor.class)
public class Processor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(LogEntity.class);

        if (elementsAnnotatedWith.isEmpty()) return true;

        Set<? extends Element> enableLogResult = roundEnv.getElementsAnnotatedWith(EnableLog.class);

        if (enableLogResult.isEmpty()) {
            log("No @EnableLog annotation found on main class");
            return true;
        }

        Element main = (Element) enableLogResult.toArray()[0];
        Context.packageName = processingEnv.getElementUtils().getPackageOf(main).getQualifiedName().toString();

        Context.filer = processingEnv.getFiler();
        Context.entities = elementsAnnotatedWith;

        try {
            Context.collectEntitiesOp.execute();
            Context.createEntitiesLogServicesOp.execute();
        } catch (Exception e) {
            log("Error while processing: {}", e.getMessage());
        }

        return true;
    }

    public static void write(TypeSpec execute) {
        try {
            JavaFile javaFile = JavaFile.builder(Context.packageName + ".log_entities", execute).build();
            javaFile.writeTo(Context.filer);
            log("File {} generated", execute.name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
