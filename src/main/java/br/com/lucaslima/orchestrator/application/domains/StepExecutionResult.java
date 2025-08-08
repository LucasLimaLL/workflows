package br.com.lucaslima.orchestrator.application.domains;

public record StepExecutionResult(String name, int statusCode, Object body) {}
