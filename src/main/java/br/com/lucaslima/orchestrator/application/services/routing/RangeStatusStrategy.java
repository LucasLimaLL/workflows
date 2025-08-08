package br.com.lucaslima.orchestrator.application.services.routing;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class RangeStatusStrategy implements NextStepResolutionStrategy {
    private static final Pattern RANGE = Pattern.compile("\\d{3}\\s*-\\s*\\d{3}");

    @Override
    public Optional<String> resolve(Map<String, String> routingRulesMap, int statusCode) {
        return routingRulesMap.entrySet().stream()
                .filter(e -> RANGE.matcher(e.getKey()).matches())
                .filter(e -> {
                    String[] parts = e.getKey().split("\\s*-\\s*");
                    int a = Integer.parseInt(parts[0]);
                    int b = Integer.parseInt(parts[1]);
                    int min = Math.min(a, b);
                    int max = Math.max(a, b);
                    return statusCode >= min && statusCode <= max;
                })
                .map(java.util.Map.Entry::getValue)
                .findFirst();
    }

    @Override public int order() { return 30; }
}
