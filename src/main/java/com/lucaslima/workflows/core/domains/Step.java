package com.lucaslima.workflows.core.domains;

import java.util.Map;

public record Step(
        String name,
        Type type,
        String method,
        String target,
        Map<String, Object> payload,
        Map<String, Object> queryParameters,
        Map<String, Object> pathParameters,
        Map<String, Object> messageBody,
        String response,
        String authProvider,
        Map<String, Object> headers
) {
    public Step {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException("Method cannot be null or blank");
        }
        if (target == null || target.isBlank()) {
            throw new IllegalArgumentException("Target cannot be null or blank");
        }
    }
}
