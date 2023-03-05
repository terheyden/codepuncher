package com.terheyden.templates;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.vavr.control.Try;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * YamlMapperTest unit tests.
 */
class YamlMapperTest {

    private static final String SIMPLE_YAML_FN = "src/test/resources/simple.yaml";

    private static final String SIMPLE_YAML = Try.of(() -> Files.readString(Path.of(SIMPLE_YAML_FN))).get();

    @Test
    void test() {

        Map<String, Object> yamlMap = YamlMapper.yamlToMap(SIMPLE_YAML);
        assertThat(yamlMap).hasSizeGreaterThan(10);

        // Is the map mutable?
        yamlMap.put("hey", "now");
    }
}
