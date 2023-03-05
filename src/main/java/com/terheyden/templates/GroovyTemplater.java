package com.terheyden.templates;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import groovy.lang.Writable;
import groovy.text.GStringTemplateEngine;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Groovy-powered text templating.
 * Specifying variable maps, Groovy templates, and combining the two.
 */
public final class GroovyTemplater {

    private static final Logger LOG = getLogger(GroovyTemplater.class);

    // Any backslash NOT at the EOL (those are special to Groovy).
    private static final Pattern JAVA_ESCAPES = Pattern.compile("\\\\.");

    /**
     * Why SimpleTemplateEngine and not e.g. GStringTemplateEngine?
     * GStringTemplateEngine (and others) are streaming, which makes their
     * syntax clunkier to use. SimpleTemplateEngine is easier to read and work with.
     */
    private static final SimpleTemplateEngine ENGINE = new SimpleTemplateEngine();

    /**
     * Extra methods related to Templates.
     */
    private static final String GROOVY_EXTRAS = FileUtils2.readResource("GroovyExtras.groovy");

    private GroovyTemplater() {
        // Private since this class shouldn't be instantiated.
    }

    /**
     * Wraps {@link GStringTemplateEngine}, providing Groovy-powered text templating.
     * @param template Groovy template to be evaluated
     * @param varMap key-value pairs to be injected into the template environment as variables
     * @return the result of evaluating the template and injecting the vars
     */
    public static String generate(String template, Map<String, Object> varMap) {
        try {

            String enhancedTemplate = enhanceGroovyTemplate(template);
            String combinedTemplate = combineGroovyTemplateScripts(GROOVY_EXTRAS, enhancedTemplate);
            LOG.debug("Injecting template: {}", combinedTemplate);
            LOG.debug("Injecting vars: {}", varMap);

            Template compiledTemplate = ENGINE.createTemplate(combinedTemplate);

            // Even though we don't return it, the binding map must be mutable
            // because the template engine will try to write to it (depending on the template).
            HashMap<String, Object> mutableVarMap = new HashMap<>(varMap);
            Writable writableResult = compiledTemplate.make(mutableVarMap);
            return writableResult.toString();

        } catch (Exception e) {
            return Exceptions.throwUnchecked(e);
        }
    }

    private static String enhanceGroovyTemplate(String template) {

        // JSP comments
        //template = template.replace("<%--", "<%/*");
        //template = template.replace("--%>", "*/%>");

        // No newline
        //template = template.replace("<%-", "<% ");
        //template = template.replace("-%>", " %>");

        // Escape all backslashes except those at the end of the line.
        return JAVA_ESCAPES.matcher(template).replaceAll("\\\\$0");
    }

    /**
     * Combine together pre-init scripts to be run (in order) with the template in the Groovy template engine
     * ({@link GroovyTemplater#generate(String, Map)}).
     * <p>
     * It's assumed that the last script is the actual template, e.g.:
     * <pre>
     * {@code
     *     combineGroovyTemplateScripts(initScript1, initScript2, template);
     * }
     * </pre>
     */
    private static String combineGroovyTemplateScripts(List<String> scripts) {

        if (scripts.isEmpty()) {
            return "";
        }

        if (scripts.size() == 1) {
            return scripts.get(0);
        }

        // We're really careful with the spaces and newlines
        // because we don't want to impact the output of the template.

        StringBuilder builder = new StringBuilder();

        // Don't surround the last script; it's the template text.
        for (int index = 0; index < scripts.size(); index++) {

            if (index < scripts.size() - 1) {
                builder.append("<% ").append(scripts.get(index)).append(" %>");
            } else {
                builder.append(scripts.get(index));
            }
        }

        return builder.toString();
    }

    private static String combineGroovyTemplateScripts(String... scripts) {
        return combineGroovyTemplateScripts(Arrays.asList(scripts));
    }
}
