package com.terheyden.templates;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.typesafe.config.Config;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HoconMapperTest unit tests.
 */
public class HoconMapperTest {

    private static final Path RESOURCES = Paths.get("src/test/resources/hocon");
    private static final Path SIMPLE_CONF = RESOURCES.resolve("simple.conf");
    private static final String SIMPLE_STR = """
        name = Cora
        age = 12
        enabled = true
        address {
            city = Oakland
        }
        """;

    @Test
    public void parseFile_good_success() {
        Config config = HoconMapper.parseFile(SIMPLE_CONF);
        validateSimpleConfig(config);
    }

    @Test
    public void parseString_good_success() {
        Config config = HoconMapper.parseString(SIMPLE_STR);
        validateSimpleConfig(config);
    }

    @Test
    public void configToMap_good_success() {

        Config config = HoconMapper.parseFile(SIMPLE_CONF);
        Map<String, Object> map = HoconMapper.configToMap(config);

        assertThat(map.get("name")).isEqualTo("Cora");
        assertThat(map.get("age")).isEqualTo(12);
        assertThat(map.get("enabled")).isEqualTo(true);

        Map<String, Object> address = (Map<String, Object>) map.get("address");
        assertThat(address.get("city")).isEqualTo("Oakland");
    }

    private static void validateSimpleConfig(Config config) {
        validateKey(config, "name", "Cora");
        validateKey(config, "age", 12);
        validateKey(config, "enabled", true);
        validateKey(config, "address.city", "Oakland");
    }

    private static void validateKey(Config config, String key, Object expected) {
        assertThat(config.getAnyRef(key)).isEqualTo(expected);
    }
}
