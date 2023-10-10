package com.poczinha.log.processor.util;

import com.poczinha.log.annotation.LogField;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Util {

    private static final DecimalFormat DF = new DecimalFormat("#.################");

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
        return !Objects.equals(value1, value2);
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
            return obNotEquals(DF.format(obj), DF.format(obj2));
        }
        throw new RuntimeException("Object " + obj + " is not a number");
    }

    public static boolean isNumericType(TypeMirror typeMirror) {
        return typeMirror.getKind() == TypeKind.DOUBLE
                || typeMirror.getKind() == TypeKind.FLOAT
                || typeMirror.getKind() == TypeKind.INT
                || typeMirror.getKind() == TypeKind.LONG
                || typeMirror.getKind() == TypeKind.SHORT
                || typeMirror.toString().equals("java.math.BigDecimal");
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
        String name = element.getSimpleName().toString();
        while (element.getKind() != ElementKind.PACKAGE) {
            element = element.getEnclosingElement();
        }
        return ((PackageElement) element).getQualifiedName().toString() + "." + name;
    }

    public static boolean isSpringDataInterface(TypeMirror superInterface) {
        String fullClassName = ((DeclaredType) superInterface).asElement().toString();
        return fullClassName.equals("org.springframework.data.jpa.repository.JpaRepository")
                || fullClassName.equals("org.springframework.data.repository.CrudRepository")
                || fullClassName.equals("org.springframework.data.repository.PagingAndSortingRepository");
    }

    public static TypeMirror getFirstTypeArgument(DeclaredType declaredType) {
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        return typeArguments.isEmpty() ? null : typeArguments.get(0);
    }

    public static FieldSpec buildFieldBean(Class<?> type, String name) {
        return FieldSpec.builder(type, name)
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(Autowired.class)
                .build();
    }

    public static FieldSpec buildFieldBean(ClassName className, String name) {
        return FieldSpec.builder(className, name)
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(Autowired.class)
                .build();
    }

    public static FieldSpec buildFieldValue(Class<?> clazz, String name, String value) {
        AnnotationSpec valueAnnotation = AnnotationSpec.builder(Value.class)
                .addMember("value", "$S", value)
                .build();

        return FieldSpec.builder(clazz, name)
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(valueAnnotation)
                .build();
    }

    public static String toUpperSnakeCase(String input) {
        input = normalizeStr(input);
        input = input.replace(" ", "_");
        return input.replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
    }

    public static String normalizeStr(String input) {
        input = Normalizer.normalize(input, Normalizer.Form.NFD);
        return input.replaceAll("[\\p{M}]", "");
    }

    public static EntityEntry getEntityEntry(SessionImplementor session, Object entity) {
        return session.getPersistenceContext().getEntry(entity);
    }

    public static EntityPersister getEntityPersister(SessionImplementor session, Object entity) {
        return session.getEntityPersister(null, entity);
    }

    public static int getIndexFromPropertyName(EntityPersister persister, String propertyName) {
        String[] propertyNames = persister.getPropertyNames();
        for (int i = 0; i < propertyNames.length; i++) {
            if (propertyNames[i].equals(propertyName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Property name not found: " + propertyName);
    }

    public static boolean notContainsRole(String role) {
        return role == null || role.isEmpty();
    }
}
