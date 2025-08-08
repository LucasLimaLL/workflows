package br.com.lucaslima.orchestrator.application.domains;

import java.util.Map;

public record HttpQueryParams(Map<String, Object> params) {}
