package com.lucaslima.workflows.commons;

import java.util.Map;
import java.util.stream.Collectors;

public enum JsonPathUtils {
    INSTANCE;

    public Object resolve(String jsonPath, Map<String, Object> context) {
        if (jsonPath == null || !jsonPath.startsWith("$.")) return null;

        String[] keys = jsonPath.substring(2).split("\\.");
        Object current = context;

        for (String key : keys) {
            if (!(current instanceof Map<?, ?> map)) return null;
            current = map.get(key);
            if (current == null) return null;
        }

        return current;
    }

    public Object resolveObject(Object input, Map<String, Object> context) {
        if (input instanceof String str && str.startsWith("$.")) {
            return resolve(str, context);
        }
        return input;
    }

    public Map<String, String> resolveMap(Map<String, Object> input, Map<String, Object> context) {
        return input == null ? Map.of() : input.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> String.valueOf(resolveObject(e.getValue(), context))
                ));
    }
}