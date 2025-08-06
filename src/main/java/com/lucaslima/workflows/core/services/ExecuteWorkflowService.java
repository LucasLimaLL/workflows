package com.lucaslima.workflows.core.services;

import com.lucaslima.workflows.core.domains.Result;
import com.lucaslima.workflows.core.domains.Step;
import com.lucaslima.workflows.core.domains.Type;
import com.lucaslima.workflows.core.ports.out.StepExecutorPort;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExecuteWorkflowService {

    private final Map<String, Step> steps;
    private final Map<Type, StepExecutorPort> executors;

    public ExecuteWorkflowService(List<Step> steps, List<StepExecutorPort> executors) {
        this.steps = steps.stream().collect(Collectors.toMap(Step::name, Function.identity()));
        this.executors = executors.stream().collect(Collectors.toMap(StepExecutorPort::getType, Function.identity()));
    }

    public Result run(String stepName, Map<String, Object> context) {
        Step step = steps.getOrDefault(stepName, null);
        StepExecutorPort executor = executors.get(step.type());

        return executor.execute(step, context);
    }

    public Step getStep(String stepName) {
        return steps.getOrDefault(stepName, null);
    }

    public String getFirstStep() {
        return steps.keySet().stream().findFirst().orElseThrow();
    }

    public List<String> getStepNames() {
        return steps.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new))
                .keySet()
                .stream()
                .toList();
    }
}
