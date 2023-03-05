package com.terheyden.templates;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

import io.vavr.control.Try;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Hello world.
 */
@SuppressWarnings("PMD.UnusedPrivateField") // They're used by PicoCli.
@Command(
    name = "Templates",
    description = "Generate files from Groovy templates")
public final class MainApp implements Callable<Integer> {

    private static final Logger LOG = getLogger(MainApp.class);

    /**
     * PicoCli will show help if this is true.
     */
    @Option(names = {"-h", "--help"}, description = "Show this help and exit", usageHelp = true)
    private boolean showHelp;

    @Option(names = {"-t", "--template"}, description = "A single template file or directory of template files")
    private Optional<Path> template = Optional.empty();

    @Option(names = {"-o", "--output"}, description = "Output file or directory")
    private Optional<Path> output = Optional.empty();

    @Option(names = {"-p", "--properties"}, description = "Properties file to use for template values; can be JSON, HOCON, or YAML")
    private Optional<Path> properties = Optional.empty();

    private MainApp() {
        // Private constructor since this shouldn't be instantiated.
    }

    /**
     * Instrument PicoCli - this is all boilerplate stuff.
     */
    public static void main(String... args) {
        int exitCode = new CommandLine(new MainApp()).execute(args);
        System.exit(exitCode);
    }

    /**
     * PicoCli - main point of entry.
     */
    @Override
    public Integer call() {
        try {

            if (showHelp) {
                return 0;
            }

            Map<String, Object> props = calculateProperties();

            Templates.generate(
                template.orElseThrow(() -> new IllegalArgumentException("Bad template path")),
                output.orElseThrow(() -> new IllegalArgumentException("Bad output path")),
                props);

            return 0;

        } catch (Exception e) {
            LOG.error("Exception during generation.", e);
            return 1;
        }
    }

    private Map<String, Object> calculateProperties() {

        return Try
            // Make sure PicoCli could parse the string into a Path obj.
            .of(() -> properties.orElseThrow(() -> new IllegalArgumentException("Invaid properties file path")))
            // We want to pass 'path' to potentially both the HoconMapper and YamlMapper, so we need to flatMap.
            .flatMap(path -> Try
                // Can we parse it into HOCON / JSON?
                .of(() -> HoconMapper.parseFile(path))
                .map(HoconMapper::configToMap)
                .orElse(Try
                    // If that failed, can we parse it as YAML?
                    .of(() -> FileUtils2.readFile(path))
                    .map(YamlMapper::yamlToMap)
                )
            ).getOrElseThrow(() -> new IllegalArgumentException("Could not parse the properties file"));
    }
}
