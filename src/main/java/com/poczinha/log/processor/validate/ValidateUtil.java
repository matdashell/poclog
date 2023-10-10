package com.poczinha.log.processor.validate;

import com.poczinha.log.annotation.LogField;
import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.mapping.FieldMapping;
import com.poczinha.log.processor.util.PrefixLogger;
import org.springframework.data.mapping.MappingException;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ValidateUtil {

    private static final PrefixLogger log = new PrefixLogger(ValidateUtil.class);

    @SafeVarargs
    public static void containsAnnotation(Element element, Class<? extends Annotation>... classes) {
        for (Class<? extends Annotation> clazz : classes) {
            if (element.getAnnotation(clazz) == null) {
                String msg = "Element ´" + element.getSimpleName() + "´ has no annotation " + clazz.getSimpleName();
                log.error(msg);
                throw new MappingException(msg);
            }
        }
    }

    public static void validField(VariableElement field, Element entity) {
        try {
            containsAnnotation(field, LogField.class);
        } catch (MappingException e) {
            throw new MappingException("Field ´" + field.getSimpleName() + "´ in class ´" + entity.getSimpleName() + "´ must be annotated with @LogField");
        }
    }

    public static void containsUniquesEntities() {
        List<String> entityNames = Context.mappings.stream()
                .map(EntityMapping::getEntityName)
                .collect(Collectors.toList());

        ArrayList<String> repeatedStrings = countRepeattedEntities(entityNames);

        if (!repeatedStrings.isEmpty()) {
            String msg = "Entities " + repeatedStrings + " are repeated";
            log.error(msg);
            throw new MappingException(msg);
        }
    }

    public static void containsUniquesFields() {
        for (EntityMapping mapping : Context.mappings) {
            List<String> fieldNames = mapping.getFields().stream()
                    .map(FieldMapping::getName)
                    .collect(Collectors.toList());

            ArrayList<String> repeatedStrings = countRepeattedEntities(fieldNames);

            if (!repeatedStrings.isEmpty()) {
                String msg = "Fields " + repeatedStrings + " are repeated in entity named " + mapping.getEntityName();
                log.error(msg);
                throw new MappingException(msg);
            }
        }
    }

    private static ArrayList<String> countRepeattedEntities(List<String> fieldNames) {
        ArrayList<String> repeatedStrings = new ArrayList<>();
        HashMap<String, Integer> counter = new HashMap<>();

        for (String str : fieldNames) {
            counter.put(str, counter.getOrDefault(str, 0) + 1);
        }

        for (String key : counter.keySet()) {
            if (counter.get(key) > 1) {
                repeatedStrings.add(key);
            }
        }
        return repeatedStrings;
    }

    public static void validateSingleIdField(FieldMapping currentIdField, VariableElement element) {
        if (currentIdField != null) {
            throw new MappingException("Entity '" + element.getSimpleName() + "' has more than one id field");
        }
    }
}
