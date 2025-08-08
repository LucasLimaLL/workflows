package br.com.lucaslima.orchestrator.application.services.routing;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class ListStatusStrategy implements NextStepResolutionStrategy {
    @Override
    public Optional<String> resolve(Map<String, String> routingRulesMap, int statusCode) {
        String target = Integer.toString(statusCode);
        return routingRulesMap.entrySet().stream()
                .filter(e -> e.getKey().contains(","))
                .filter(e -> Arrays.stream(e.getKey().split("\\s*,\\s*")).anyMatch(k -> k.equals(target)))
                .map(java.util.Map.Entry::getValue)
                .findFirst();
    }
    @Override public int order() { return 20; }
}
