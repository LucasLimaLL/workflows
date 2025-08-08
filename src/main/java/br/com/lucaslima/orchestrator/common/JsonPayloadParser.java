package br.com.lucaslima.orchestrator.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public final class JsonPayloadParser {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private JsonPayloadParser(){}

    public static Map<String, Object> parse(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("payload inválido: " + e.getMessage(), e);
        }
    }
}
