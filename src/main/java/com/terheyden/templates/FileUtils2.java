package com.terheyden.templates;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import io.vavr.control.Try;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * FileUtils2 class.
 */
public final class FileUtils2 {

    private static final Logger LOG = getLogger(FileUtils2.class);

    private FileUtils2() {
        // Private since this class shouldn't be instantiated.
    }

    /**
     * Read a text file into a String.
     */
    public static String readFile(Path file) {
        try {
            return Files.readString(file);
        } catch (Exception e) {
            return Exceptions.throwUnchecked(e);
        }
    }

    public static Stream<Path> findFiles(Path startDir, int maxDepth) {
        try {

            if (Files.notExists(startDir)) {
                throw new IllegalArgumentException("Start dir doesn't exist: " + startDir);
            }

            if (!Files.isDirectory(startDir)) {
                throw new IllegalArgumentException("Start dir isn't a directory: " + startDir);
            }

            return Files
                .walk(startDir, maxDepth)
                .filter(Files::isRegularFile);
        } catch (IOException e) {
            return Exceptions.throwUnchecked(e);
        }
    }

    public static Stream<Path> findFiles(Path startDir) {
        return findFiles(startDir, Integer.MAX_VALUE);
    }

    public static Stream<Path> findFilesByName(Path startDir, String filenameRegex) {

        Pattern filenamePat = Pattern.compile(filenameRegex);

        return findFiles(startDir)
            .filter(path -> filenamePat.matcher(path.getFileName().toString()).find());
    }

    public static Stream<Path> findDirs(Path startDir, int maxDepth) {
        try {

            return Files
                .walk(startDir, maxDepth)
                .filter(Files::isDirectory);
        } catch (IOException e) {
            return Exceptions.throwUnchecked(e);
        }
    }

    public static Stream<Path> findDirs(Path startDir) {
        return findDirs(startDir, Integer.MAX_VALUE);
    }

    public static Stream<Path> findDirsByName(Path startDir, String dirNameRegex) {

        Pattern dirNamePat = Pattern.compile(dirNameRegex);

        return findDirs(startDir)
            .filter(path -> dirNamePat.matcher(path.getFileName().toString()).find());
    }

    /**
     * Find all files but track their path relative to the start dir.
     */
    public static Stream<RelativeFile> findAllFiles(Path startDir) {
        return findFiles(startDir)
            .peek(found -> LOG.debug("Found file: {}", found))
            .map(found -> new RelativeFile(startDir, found));
    }

    /**
     * For when the user wants to inject a single template file.
     */
    public static Stream<RelativeFile> findOneFileWithInfo(Path sourceFile) {
        return Stream
            .of(sourceFile)
            .map(RelativeFile::new);
    }

    /**
     * Simplistic grep we can use to find GString usages, so we can generate
     * settings and sandboxes for the user.
     */
    public static List<String> grep(Path startDir, String regex, int context) {

        return findFiles(startDir)
            .flatMap(file -> grepFile(file, regex, context))
            .toList();
    }

    private static Stream<String> grepFile(Path file, String regex, int context) {
        try {

            List<String> snips = new ArrayList<>();
            List<String> lines = Files.readAllLines(file);
            Pattern pattern = Pattern.compile(regex);

            for (int index = 0; index < lines.size(); index++) {

                String line = lines.get(index);

                if (!pattern.matcher(line).find()) {
                    continue;
                }

                StringBuilder snip = new StringBuilder();

                // Add the context lines above.
                for (int i = index - context; i < index; i++) {
                    if (i >= 0) {
                        snip.append(lines.get(i)).append('\n');
                    }
                }

                // Add the matching line.
                snip.append(line).append('\n');

                // Add the context lines below.
                for (int i = index + 1; i <= index + context; i++) {
                    if (i < lines.size()) {
                        snip.append(lines.get(i)).append('\n');
                    }
                }

                snips.add(snip.toString());
            }

            return snips.stream();

        } catch (Exception e) {
            return Exceptions.throwUnchecked(e);
        }
    }

    public static void deleteEntireDir(Path dir) {
        try {

            // First delete the files.
            List<Path> allFiles = findFiles(dir).toList();
            for (Path file : allFiles) {
                LOG.debug("Cleaning: {}", file);
                Files.delete(file);
            }

            // Then delete the subdirs.
            List<Path> allDirs = findDirs(dir)
                .sorted((dir1, dir2) -> dir2.getNameCount() - dir1.getNameCount())
                .toList();

            for (Path subDir : allDirs) {
                LOG.debug("Cleaning: {}", subDir);
                Files.delete(subDir);
            }

        } catch (Exception e) {
            Exceptions.throwUnchecked(e);
        }
    }

    /**
     * Read a JAR resource file in as a String.
     * @throws IOException if the resource cannot be found
     */
    public static String readResource(String resourcePath) {
        try {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream stream = loader.getResourceAsStream(resourcePath);

            if (stream != null) {
                return IOUtils.toString(stream, StandardCharsets.UTF_8);
            }

            throw new IOException("Resource not found: " + resourcePath);

        } catch (IOException e) {
            return Exceptions.throwUnchecked(e);
        }
    }

    public static void writeFile(Path saveFile, String text) {
        try {

            Files.writeString(
                saveFile,
                text,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,             // Create if not exists.
                StandardOpenOption.TRUNCATE_EXISTING); // Clobber if it exists.

        } catch (Exception e) {
            Exceptions.throwUnchecked(e);
        }
    }

    /**
     * Load a file of settings stored in the Java Properties format.
     * Convert the Properties to a Map, and return it.
     * https://en.wikipedia.org/wiki/.properties
     */
    public static Map<String, Object> loadProperties(Path propsFile) {

        if (Files.notExists(propsFile)) {
            throw new IllegalArgumentException("Properties file doesn't exist: " + propsFile);
        }

        Properties props = new Properties();

        Try.run(() -> props.load(Files.newInputStream(propsFile)))
            .onFailure(Exceptions::throwUnchecked);

        // Convert to a map for use in our templates.
        // Make sure to iterate in order, to retain precedence.
        Map<String, Object> varMap = new HashMap<>(props.size());
        props.forEach((key, val) -> varMap.put(key.toString(), val));

        LOG.debug("Loaded properties: {}", varMap);

        return varMap;
    }

    /**
     * Used by the CLI — is the string they gave us a real file or dir?
     */
    public static Optional<Path> parseExistingPath(String pathStr) {
        try {

            Path path = Paths.get(pathStr);
            return Files.exists(path) ? Optional.of(path) : Optional.empty();

        } catch (Exception ignore) {
            return Optional.empty();
        }
    }
}
