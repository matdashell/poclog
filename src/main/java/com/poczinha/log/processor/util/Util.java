package com.poczinha.log.processor.util;

import com.poczinha.log.processor.annotation.LogEntity;
import com.poczinha.log.processor.annotation.LogField;

import javax.lang.model.element.Element;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.lang.annotation.Annotation;

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

    public static boolean notEquals(Object value1, Object value2) {
        if (value1 != null && value2 != null) {
            return !value1.equals(value2);
        }
        return value1 != value2;
    }
}
