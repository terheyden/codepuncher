package com.terheyden.templates;

import java.nio.file.Path;
import java.util.Map;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

/**
 * HoconMapper class.
 */
public final class HoconMapper {

    private HoconMapper() {
        // Private since this class shouldn't be instantiated.
    }

    public static Config parseFile(Path file) {
        return ConfigFactory.parseFile(file.toFile());
    }

    public static Config parseString(String hocon) {
        return ConfigFactory.parseString(hocon);
    }

    public static Map<String, Object> configToMap(Config config) {
        return config.resolve().root().unwrapped();
    }

    public static String configToHoconString(Config config) {
        return config.resolve().root().render();
    }

    public static String configToJsonString(Config config) {
        // Turn off HOCON-specific features and render as JSON.
        return config.resolve().root().render(ConfigRenderOptions.concise());
    }
}
