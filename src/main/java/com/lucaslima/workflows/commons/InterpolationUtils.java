package com.lucaslima.workflows.commons;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum InterpolationUtils {
    INSTANCE;

    private static final Pattern INTERPOLATION = Pattern.compile("\\$\\{([^:}]+)(?::([^}]*))?}");

    public String interpolate(String input, Map<String, Object> context) {
        if (input == null || !input.contains("${")) return input;

        Matcher matcher = INTERPOLATION.matcher(input);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String path = matcher.group(1);
            String defaultValue = matcher.group(2);

            Object value = JsonPathUtils.INSTANCE.resolve("$.%s".formatted(path), context);
            if (value == null) value = defaultValue;

            matcher.appendReplacement(sb, Matcher.quoteReplacement(String.valueOf(value)));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    public String resolveUrlWithPathVariables(String url, Map<String, Object> pathVars) {
        if (url == null || pathVars == null) return url;

        for (Map.Entry<String, Object> entry : pathVars.entrySet()) {
            url = url.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }

        return url;
    }
}
