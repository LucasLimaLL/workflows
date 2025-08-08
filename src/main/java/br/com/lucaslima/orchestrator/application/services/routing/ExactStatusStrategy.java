package br.com.lucaslima.orchestrator.application.services.routing;

import java.util.Map;
import java.util.Optional;

public class ExactStatusStrategy implements NextStepResolutionStrategy {
    @Override
    public Optional<String> resolve(Map<String, String> routingRulesMap, int statusCode) {
        String key = Integer.toString(statusCode);
        return Optional.ofNullable(routingRulesMap.get(key));
    }
    @Override public int order() { return 10; }
}
