package br.com.lucaslima.orchestrator.application.domains;

import java.util.Map;

public record HttpPathVariables(Map<String, String> vars) {}
