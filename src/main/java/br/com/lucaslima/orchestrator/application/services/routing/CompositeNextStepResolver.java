package br.com.lucaslima.orchestrator.application.services.routing;

import br.com.lucaslima.orchestrator.application.domains.NextStepRouting;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CompositeNextStepResolver {

    private final List<NextStepResolutionStrategy> orderedStrategies;

    public CompositeNextStepResolver(List<NextStepResolutionStrategy> strategyList) {
        this.orderedStrategies = strategyList.stream()
                .sorted(Comparator.comparingInt(NextStepResolutionStrategy::order))
                .toList();
    }

    public Optional<String> resolve(NextStepRouting nextStepRouting, int statusCode) {
        if (nextStepRouting == null || nextStepRouting.routes() == null || nextStepRouting.routes().isEmpty()) {
            return Optional.empty();
        }
        Map<String, String> rulesMap = nextStepRouting.routes();
        for (NextStepResolutionStrategy strategy : orderedStrategies) {
            Optional<String> maybe = strategy.resolve(rulesMap, statusCode);
            if (maybe.isPresent()) return maybe;
        }
        return Optional.empty();
    }
}
