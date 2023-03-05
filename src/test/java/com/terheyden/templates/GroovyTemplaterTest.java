package com.terheyden.templates;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * https://groovy-lang.org/templating.html
 */
public class GroovyTemplaterTest {

    private static final Logger LOG = getLogger(GroovyTemplaterTest.class);

    // Testing var map with a medley of types.
    private final Map<String, Object> varMap = Map.of(
            "greeting", "Hello",
            "user", new TestUser("Cora", 12),
            "LOG", LOG
        );

    void generate_immutableVarMap_succeeds() {

        String template = "${greeting} ${user.name}!";
        String result = GroovyTemplater.generate(template, varMap);

        assertThat(result).isEqualTo("Hello Cora!");
    }

    @Test
    public void testGroovy() {

        LOG.info(GroovyTemplater.generate("""

            Uppercase = ${user.name.toUpperCase()}

            """, varMap));
    }

    @Test
    public void testImports() {

        // Fails — groovy.json.JsonOutput is not imported.
        assertThatExceptionOfType(GroovyRuntimeException.class).isThrownBy(() -> {
            GroovyTemplater.generate("""
                User = ${JsonOutput.toJson(user)}
                """, varMap);
        });

        // Simple solution — specify full path.
        assertThatNoException().isThrownBy(() -> {
            GroovyTemplater.generate("""
                User = ${groovy.json.JsonOutput.toJson(user)}
                """, varMap);
        });

        // Another solution — import the class.
        assertThatNoException().isThrownBy(() -> {
            GroovyTemplater.generate("""
                <%
                import groovy.json.JsonOutput
                %>
                User = ${JsonOutput.toJson(user)}
                """, varMap);
        });

        // Another solution — import the package.
        assertThatNoException().isThrownBy(() -> {
            GroovyTemplater.generate("""
                <%
                import groovy.json.*
                %>
                User = ${JsonOutput.toJson(user)}
                """, varMap);
        });
    }

    @Test
    public void testTemplating() {

        String template = """
            $greeting ${user.name}!
            You are ${user.age} years old.
            You are ${user.age >= 18 ? "an adult" : "a minor"}.""";

        String result = GroovyTemplater.generate(template, varMap);

        assertThat(result).isEqualTo("""
            Hello Cora!
            You are 12 years old.
            You are a minor.""");
    }

    @Test
    public void testExceptionHandling() {

        assertThatExceptionOfType(GroovyRuntimeException.class)
            .isThrownBy(() -> GroovyTemplater.generate("I am ${broken!", varMap));
    }

    /**
     * Groovy scripting supports:
     *   GStrings: "Hello ${name}"
     *   JSP scriptlet syntax:
     *     "Hello <%= name %>"
     *     "Hello <% print name %>"
     */
    @Test
    public void testScriptleting() {

        String result = GroovyTemplater.generate(
            "My name is <%= user.name %> and I am <% print user.age %> years old!",
            varMap);

        assertThat(result).isEqualTo("My name is Cora and I am 12 years old!");
    }

    @Test
    public void groovySandbox() {
        new GroovyShell().evaluate("""

            // Groovy is a dynamic language, so you don't need to declare types.

            """);
    }
}
