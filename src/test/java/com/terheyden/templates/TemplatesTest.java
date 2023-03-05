package com.terheyden.templates;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TemplatesTest unit tests.
 */
class TemplatesTest {

    private static final Path RESOURCES = Paths.get("src/test/resources");
    private static final Path SIMPLE_DIR = RESOURCES.resolve("template-simple");
    private static final Path SIMPLE_SRC = SIMPLE_DIR.resolve("src");

    private final Map<String, Object> varMap = Map.of(
        "name", "Cora",
        "age", 12
    );

    @Test
    void generateAllTemplateFiles_validInputs_validOutputs(@TempDir Path testOutDir) {

        assertThat(SIMPLE_SRC).isDirectory();
        assertThat(testOutDir).isDirectory();
        Templates.generate(SIMPLE_SRC, testOutDir, varMap);

        assertThat(testOutDir.resolve("README.md"))
            .exists()
            .hasContent("""
                # Cora

                Your name is Cora, and you are 12 years old.
                """);

        assertThat(testOutDir.resolve("etc/hello.md"))
            .exists()
            .hasContent("""
                # CORA

                Hello.
                """);
    }
}
