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

@SupportedAnnotationTypes({"com.poczinha.log.processor.annotation.*",})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(javax.annotation.processing.Processor.class)
public class Processor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("Processor called");

        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(LogEntity.class);

        if (elementsAnnotatedWith.isEmpty()) return true;

        Element main = (Element) roundEnv.getElementsAnnotatedWith(EnableLog.class).toArray()[0];
        Context.packageName = processingEnv.getElementUtils().getPackageOf(main).getQualifiedName().toString();

        EnableLog annotation = main.getAnnotation(EnableLog.class);
        Context.entitiesBasePackage = annotation.baseEntityPagackage();
        Context.repositoriesBasePackage = annotation.baseRepositoryPackage();

        Context.filer = processingEnv.getFiler();
        Context.entities = elementsAnnotatedWith;

        try {
            Context.collectEntitiesOp.execute();
            Context.createEntitiesLogServicesOp.execute();
            Context.configureOp.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return true;
    }

    public static void write(TypeSpec execute) {
        try {
            JavaFile javaFile = JavaFile.builder(Context.packageName, execute).build();
            javaFile.writeTo(Context.filer);
            System.out.println("File written");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
