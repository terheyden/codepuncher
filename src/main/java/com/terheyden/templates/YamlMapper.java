package com.terheyden.templates;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * For reading in a map, or settings, or whatever, from a YAML file.
 */
public final class YamlMapper {

    private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory())
        .findAndRegisterModules();

    private static final TypeReference<Map<String, Object>> MAP_STR_OBJ = new TypeReference<>() {
    };

    private YamlMapper() {
        // Private since this class shouldn't be instantiated.
    }

    public static Map<String, Object> yamlToMap(String yaml) {
        try {
            return YAML.readValue(yaml, MAP_STR_OBJ);
        } catch (JsonProcessingException e) {
            return Exceptions.throwUnchecked(e);
        }
    }

    public static <T> T yamlToObj(String yaml, Class<T> clazz) {
        try {
            return YAML.readValue(yaml, clazz);
        } catch (JsonProcessingException e) {
            return Exceptions.throwUnchecked(e);
        }
    }

    public static <T> T yamlToObj(String yaml, TypeReference<T> typeRef) {
        try {
            return YAML.readValue(yaml, typeRef);
        } catch (JsonProcessingException e) {
            return Exceptions.throwUnchecked(e);
        }
    }

    public static String mapToYaml(Map<String, Object> map) {
        try {
            return YAML.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return Exceptions.throwUnchecked(e);
        }
    }

    public static String objToYaml(Object obj) {
        try {
            return YAML.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return Exceptions.throwUnchecked(e);
        }
    }
}
