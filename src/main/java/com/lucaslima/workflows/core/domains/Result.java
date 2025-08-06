package com.lucaslima.workflows.core.domains;

public record Result(String name, int statusCode, Object responseBody) {

    public Result {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (statusCode < 100 || statusCode > 599) {
            throw new IllegalArgumentException("Status code must be between 100 and 599");
        }
        if (responseBody == null) {
            throw new IllegalArgumentException("Response body cannot be null");
        }
    }
}
