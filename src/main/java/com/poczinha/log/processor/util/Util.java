package com.poczinha.log.processor.util;

import com.poczinha.log.processor.annotation.LogEntity;
import com.poczinha.log.processor.annotation.LogField;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.text.DecimalFormat;

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

    public static void log(String message, Object... args) {
        for (Object arg : args) {
            message = message.replaceFirst("\\{\\}", arg.toString());
        }
        System.out.println("\uD83D\uDCDC Poc log - " + message);
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

    public static boolean isIdEntity(VariableElement entity) {
        return containsAnnotation(entity, Id.class);
    }
}
