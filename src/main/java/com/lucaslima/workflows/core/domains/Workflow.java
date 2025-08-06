package com.lucaslima.workflows.core.domains;

import java.util.List;

public record Workflow(String name, String version, List<Step> steps) {

    public Workflow {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("Version cannot be null or blank");
        }
        if (steps == null || steps.isEmpty()) {
            throw new IllegalArgumentException("Steps cannot be null or empty");
        }
    }
}
