package com.terheyden.templates;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import groovy.text.SimpleTemplateEngine;

/**
 * SandboxStudio class.
 */
public class SandboxStudio {

    private final Map<String, Object> settings = Map.of(
        "projectName", "My Project",
        "projectSlug", "my-project",
        "projectId", 1,
        "projectDescription", "My project description",
        "lastModified", LocalDateTime.now()
    );

    private final SimpleTemplateEngine engine = new SimpleTemplateEngine();

    @Test
    public void test() throws IOException, ClassNotFoundException {

        // Map must be mutable.
        Map<String, Object> mutableSettings = new HashMap<>(settings);

        String result = engine.createTemplate("""
            # ${projectName}
            ${projectDescription}
            """).make(mutableSettings).toString();

        System.out.println(result);
    }
}
