package br.com.lucaslima.orchestrator.application.services.routing;

import java.util.Map;
import java.util.Optional;

public class FamilyStatusStrategy implements NextStepResolutionStrategy {
    @Override
    public Optional<String> resolve(Map<String, String> routingRulesMap, int statusCode) {
        String familyKey = (statusCode / 100) + "xx";
        return Optional.ofNullable(routingRulesMap.get(familyKey));
    }
    @Override public int order() { return 40; }
}
