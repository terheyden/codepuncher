package com.terheyden.templates;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Main code-based entry point (separate from a CLI, GUI, REST endpoint, etc.).
 * Also globals.
 */
public final class Templates {

    private static final Logger LOG = getLogger(Templates.class);

    private Templates() {
        // Private since this class shouldn't be instantiated.
    }

    public static void generate(Path sourceFileOrDir, Path saveFileOrDir, Map<String, Object> props) {

        if (Files.isRegularFile(sourceFileOrDir)) {
            generateSingleFile(sourceFileOrDir, saveFileOrDir, props);
        } else if (Files.isDirectory(sourceFileOrDir)) {
            generateDirectoryFiles(sourceFileOrDir, saveFileOrDir, props);
        } else {
            throw new IllegalArgumentException("Source does not exist: " + sourceFileOrDir.toString());
        }
    }

    private static void generateSingleFile(Path sourceFile, Path saveFile, Map<String, Object> props) {
        generateOutputFile(sourceFile, saveFile, props);
    }

    private static void generateDirectoryFiles(Path sourceDir, Path saveDir, Map<String, Object> props) {

        FileUtils2
            .findAllFiles(sourceDir)
            .forEach(found -> generateOutputFile(found.getAbsoluteFile(), found.calculateTargetFile(saveDir), props));
    }

    private static void generateOutputFile(Path sourceFile, Path outFile, Map<String, Object> props) {
        try {
            LOG.debug("Injecting and saving template file {} ==> {}", sourceFile, outFile);
            // Make sure the save dir exists.
            Files.createDirectories(outFile.toAbsolutePath().getParent());
            String templateStr = FileUtils2.readFile(sourceFile);
            String generatedStr = GroovyTemplater.generate(templateStr, props);
            FileUtils2.writeFile(outFile, generatedStr);

        } catch (Exception e) {
            Exceptions.throwUnchecked(e);
        }
    }
}
