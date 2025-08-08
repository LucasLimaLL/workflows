package br.com.lucaslima.orchestrator.application.domains;

public record Step(
    String name,
    StepType type,
    String verb,
    String target,
    StepPayload payload,
    HttpQueryParams query,
    HttpPathVariables pathVariables,
    String storeResponseAs,
    String authProvider,
    StepMetadata metadata,
    NextStepRouting nextStep,
    String condition
) {}
