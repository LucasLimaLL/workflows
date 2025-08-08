package br.com.lucaslima.orchestrator.application.services;

import br.com.lucaslima.orchestrator.application.domains.NextStepRouting;
import br.com.lucaslima.orchestrator.application.domains.Step;
import br.com.lucaslima.orchestrator.application.domains.StepExecutionResult;
import br.com.lucaslima.orchestrator.application.domains.StepType;
import br.com.lucaslima.orchestrator.application.domains.Workflow;
import br.com.lucaslima.orchestrator.application.ports.in.OrchestrationUseCase;
import br.com.lucaslima.orchestrator.application.ports.out.ExecuteStepPort;
import br.com.lucaslima.orchestrator.application.ports.out.WorkflowRepositoryPort;
import br.com.lucaslima.orchestrator.application.services.routing.CompositeNextStepResolver;
import br.com.lucaslima.orchestrator.common.ConditionExpressionEvaluator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OrchestrationService implements OrchestrationUseCase {

    private final WorkflowRepositoryPort workflowRepository;
    private final Map<StepType, ExecuteStepPort> executorsByTypeMap;
    private final CompositeNextStepResolver nextStepResolver;

    public OrchestrationService(WorkflowRepositoryPort workflowRepository,
                                java.util.List<ExecuteStepPort> executorPortList,
                                CompositeNextStepResolver nextStepResolver) {
        this.workflowRepository = workflowRepository;
        this.executorsByTypeMap = executorPortList.stream()
                .collect(Collectors.toMap(ExecuteStepPort::getType, e -> e));
        this.nextStepResolver = nextStepResolver;
    }

    @Override
    public void run(String flowId,
                    Map<String, Object> initialContextMap,
                    boolean isReprocess,
                    String lastExecutedStepName) {

        Workflow workflow = workflowRepository.load(flowId, lastExecutedStepName);
        List<Step> stepList = workflow.steps();
        if (stepList == null || stepList.isEmpty()) return;

        Map<String, Step> stepByNameMap = stepList.stream()
                .collect(Collectors.toMap(Step::name, Function.identity()));

        String currentStepName = stepList.get(0).name();

        Stream.iterate(currentStepName, Objects::nonNull, stepName -> {
            Step step = stepByNameMap.get(stepName);

            if (step.condition() != null && !ConditionExpressionEvaluator.evaluate(step.condition(), initialContextMap)) {
                return Optional.ofNullable(step.nextStep())
                        .map(NextStepRouting::routes)
                        .map(r -> r.get("notapply"))
                        .orElse(null);
            }

            ExecuteStepPort executorPort = requireExecutor(step.type());
            StepExecutionResult executionResult = executorPort.execute(step, initialContextMap, isReprocess);

            if (step.storeResponseAs() != null) {
                initialContextMap.put(step.storeResponseAs(), executionResult.body());
            }

            return nextStepResolver.resolve(step.nextStep(), executionResult.statusCode()).orElse(null);

        }).forEach(ignored -> { /* consume */ });
    }

    private ExecuteStepPort requireExecutor(StepType type) {
        ExecuteStepPort port = executorsByTypeMap.get(type);
        if (port == null) {
            throw new IllegalArgumentException("Nenhum executor cadastrado para StepType: " + type);
        }
        return port;
    }
}
