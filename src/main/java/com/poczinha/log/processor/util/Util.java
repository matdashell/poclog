package com.poczinha.log.processor.util;

import com.poczinha.log.annotation.LogEntity;
import com.poczinha.log.annotation.LogField;
import com.poczinha.log.processor.Context;
import com.squareup.javapoet.FieldSpec;
import org.springframework.beans.factory.annotation.Autowired;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.lang.annotation.Annotation;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

public class Util {

    public static String getAccessOf(String name) {
        return "get" + name.substring(0, 1).toUpperCase() + name.substring(1) + "()";
    }

    public static boolean isIgnoreEntity(Element element) {
        LogEntity annotation = element.getAnnotation(LogEntity.class);
        return annotation != null && annotation.ignore();
    }

    public static boolean isIgnoreField(Element element) {
        boolean isRelationField = containsAnnotation(element,
                ManyToOne.class,
                OneToMany.class,
                OneToOne.class,
                ManyToMany.class
        );

        if (isRelationField) return true;

        LogField annotation = element.getAnnotation(LogField.class);
        return annotation != null && annotation.ignore();
    }

    @SafeVarargs
    public static boolean containsAnnotation(Element element, Class<? extends Annotation>... annotations) {
        for (Class<? extends Annotation> annotation : annotations) {
            if (element.getAnnotation(annotation) != null) {
                return true;
            }
        }
        return false;
    }

    public static String valueOf(Object value) {
        if (value != null) {
            return String.valueOf(value);
        }
        return null;
    }

    public static boolean obNotEquals(Object value1, Object value2) {
        if (value1 != null && value2 != null) {
            return !value1.equals(value2);
        }
        return value1 != value2;
    }

    public static Element typeMirrorToElement(TypeMirror typeMirror) {
        if (typeMirror.getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            return declaredType.asElement();
        }
        throw new RuntimeException("TypeMirror " + typeMirror + " is not a declared type");
    }

    public static boolean nuNotEquals(Object obj, Object obj2) {
        if (obj instanceof Number && obj2 instanceof Number) {
            DecimalFormat df = new DecimalFormat("#.################");
            return obNotEquals(df.format(obj), df.format(obj2));
        }

        throw new RuntimeException("Object " + obj + " is not a number");
    }

    public static boolean isTypeNumeric(TypeMirror typeMirror) {
        return typeMirror.getKind() == TypeKind.DOUBLE
                || typeMirror.getKind() == TypeKind.FLOAT
                || typeMirror.getKind() == TypeKind.INT
                || typeMirror.getKind() == TypeKind.LONG
                || typeMirror.getKind() == TypeKind.SHORT
                || typeMirror.toString().equals("java.math.BigDecimal");
    }

    public static void setBasePackages(Set<? extends Element> entities, Set<? extends Element> repositories) {
        Context.entitiesBasePackages = findCommonBasePackage(entities);
        Context.repositoriesBasePackages = findCommonBasePackage(repositories);
    }

    public static String findCommonBasePackage(Set<? extends Element> elements) {
        if (elements.isEmpty()) {
            return "";
        }

        String[] firstPackageParts = elements.iterator().next().getEnclosingElement().toString().split("\\.");

        StringBuilder commonBasePackage = new StringBuilder();
        for (int i = 0; i < firstPackageParts.length; i++) {
            String currentPart = firstPackageParts[i];

            for (Element element : elements) {
                String[] packageParts = element.getEnclosingElement().toString().split("\\.");
                if (packageParts.length <= i || !packageParts[i].equals(currentPart)) {
                    return commonBasePackage.toString();
                }
            }

            if (i > 0) {
                commonBasePackage.append(".");
            }
            commonBasePackage.append(currentPart);
        }

        return commonBasePackage.toString();
    }

    public static String getPackageName(Element element) {
        while (element.getKind() != ElementKind.PACKAGE) {
            element = element.getEnclosingElement();
        }
        return ((PackageElement) element).getQualifiedName().toString();
    }

    public static boolean isSpringDataInterface(TypeMirror superInterface) {
        return ((DeclaredType) superInterface).asElement().toString().startsWith("org.springframework.data");
    }

    public static TypeMirror getFirstTypeArgument(DeclaredType declaredType) {
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        return typeArguments.isEmpty() ? null : typeArguments.get(0);
    }

    public static FieldSpec buildFieldBean(FieldSpec.Builder registerService) {
        return registerService
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(Autowired.class)
                .build();
    }
}
