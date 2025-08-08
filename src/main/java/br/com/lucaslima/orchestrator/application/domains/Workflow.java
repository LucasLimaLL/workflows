package br.com.lucaslima.orchestrator.application.domains;

import java.util.List;

public record Workflow(String name, String version, List<Step> steps, String errorStep) {}
