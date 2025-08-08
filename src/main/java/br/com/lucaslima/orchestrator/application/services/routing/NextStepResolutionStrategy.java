package br.com.lucaslima.orchestrator.application.services.routing;

import java.util.Map;
import java.util.Optional;

public interface NextStepResolutionStrategy {
    Optional<String> resolve(Map<String, String> routingRulesMap, int statusCode);
    int order(); // menor = maior prioridade
}
