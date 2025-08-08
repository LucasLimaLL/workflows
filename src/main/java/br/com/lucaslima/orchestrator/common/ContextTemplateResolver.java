package br.com.lucaslima.orchestrator.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ContextTemplateResolver {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\$\{([^:{}]+)(:([^}]*))?}");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ContextTemplateResolver() {}

    public static String resolveString(String template, Map<String, Object> contextMap) {
        if (template == null) return null;
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String expression = matcher.group(1).trim();
            String defaultValue = matcher.group(3);
            Object evaluated = eval(expression, contextMap);
            String replacement = evaluated != null ? evaluated.toString() : (defaultValue != null ? defaultValue : "");
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static Object eval(String expression, Map<String, Object> contextMap) {
        if (expression.startsWith("$.")) {
            for (Object value : contextMap.values()) {
                try {
                    Object result = JsonPath.read(value, expression);
                    if (result != null) return result;
                } catch (Exception ignored) {}
            }
        }
        return contextMap.get(expression);
    }

    public static Map<String, String> resolveHeaders(Map<String, String> headersMap, Map<String, Object> contextMap) {
        if (headersMap == null) return Map.of();
        Map<String, String> resolved = new LinkedHashMap<>();
        headersMap.forEach((k, v) -> resolved.put(k, resolveString(v, contextMap)));
        return resolved;
    }

    public static Object resolveBody(Map<String, Object> payloadMap, Map<String, Object> contextMap) {
        if (payloadMap == null) return null;
        if (payloadMap.size() == 1 && payloadMap.containsKey("$ref")) {
            String key = resolveString(String.valueOf(payloadMap.get("$ref")), contextMap);
            return contextMap.getOrDefault(key, Map.of());
        }
        return resolveObject(payloadMap, contextMap);
    }

    private static Object resolveObject(Object value, Map<String, Object> contextMap) {
        if (value instanceof String s) return resolveString(s, contextMap);
        if (value instanceof Map<?, ?> m) {
            Map<String, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : m.entrySet()) {
                result.put(e.getKey().toString(), resolveObject(e.getValue(), contextMap));
            }
            return result;
        }
        if (value instanceof List<?> l) {
            return l.stream().map(it -> resolveObject(it, contextMap)).toList();
        }
        return value;
    }

    public static String resolveUrl(String rawUrl,
                                    Map<String, Object> contextMap,
                                    Map<String, String> pathVariablesMap,
                                    Map<String, Object> queryParamsMap) {
        String url = resolveString(rawUrl, contextMap);
        if (pathVariablesMap != null) {
            for (Map.Entry<String, String> e : pathVariablesMap.entrySet()) {
                url = url.replace("{" + e.getKey() + "}", resolveString(e.getValue(), contextMap));
            }
        }
        if (queryParamsMap != null && !queryParamsMap.isEmpty()) {
            String query = queryParamsMap.entrySet().stream()
                    .map(e -> e.getKey() + "=" + URLEncoder.encode(resolveString(String.valueOf(e.getValue()), contextMap), StandardCharsets.UTF_8))
                    .collect(java.util.stream.Collectors.joining("&"));
            url = url.contains("?") ? url + "&" + query : url + "?" + query;
        }
        return url;
    }

    public static String toJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException("Erro serializando JSON", e);
        }
    }
}
