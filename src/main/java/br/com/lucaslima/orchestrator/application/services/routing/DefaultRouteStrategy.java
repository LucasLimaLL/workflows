package br.com.lucaslima.orchestrator.application.services.routing;

import java.util.Map;
import java.util.Optional;

public class DefaultRouteStrategy implements NextStepResolutionStrategy {
    @Override
    public Optional<String> resolve(Map<String, String> routingRulesMap, int statusCode) {
        return Optional.ofNullable(routingRulesMap.get("default"));
    }
    @Override public int order() { return 50; }
}
