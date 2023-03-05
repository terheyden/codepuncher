package com.terheyden.templates;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import static com.terheyden.templates.FileUtils2.findAllFiles;
import static com.terheyden.templates.FileUtils2.findFiles;
import static com.terheyden.templates.FileUtils2.grep;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * FileUtils2Test unit tests.
 */
class FileUtils2Test {

    private static final String RESOURCES = "src/test/resources/";
    private static final Path RESOURCE_DIR = Paths.get(RESOURCES);
    private static final Path SIMPLE_DIR = Paths.get(RESOURCES + "template-simple");

    @Test
    void readFile_goodFile_isRead() {
        String content = FileUtils2.readFile(RESOURCE_DIR.resolve("simple.yaml"));
        assertThat(content).contains("123 Main St");
    }

    @Test
    void testFindFiles() {

        List<RelativeFile> found = findAllFiles(SIMPLE_DIR).toList();
        assertThat(found).hasSize(3);

        List<Path> maxDepth1 = findFiles(SIMPLE_DIR, 1).toList();
        assertThat(maxDepth1).hasSize(1);
    }

    @Test
    void testGrep() {

        List<String> found = grep(RESOURCE_DIR.resolve("grep"), "\\$\\{.+?\\}", 1);
        assertThat(found).hasSize(2);
    }
}
