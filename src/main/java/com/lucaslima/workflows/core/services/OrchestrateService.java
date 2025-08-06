package com.lucaslima.workflows.core.services;

import com.lucaslima.workflows.core.domains.Result;
import com.lucaslima.workflows.core.domains.Step;
import com.lucaslima.workflows.core.domains.Workflow;
import com.lucaslima.workflows.core.ports.out.SearchWorkflowPort;
import com.lucaslima.workflows.core.ports.out.StepExecutorPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrchestrateService {

    private final List<StepExecutorPort> executors;
    private final SearchWorkflowPort searchWorkflowPort;

    public OrchestrateService(List<StepExecutorPort> executors, SearchWorkflowPort searchWorkflowPort) {
        this.executors = executors;
        this.searchWorkflowPort = searchWorkflowPort;
    }

    public void execute(String flowId, Map<String, Object> context) {

        var workflow = searchWorkflowPort.findByName(flowId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow not found"));

        ExecuteWorkflowService executeWorkflowService = new ExecuteWorkflowService(workflow.steps(), executors);
        String currentStep = executeWorkflowService.getFirstStep();

        while (currentStep != null) {
            Step step = executeWorkflowService.getStep(currentStep);
            if (step == null) {
                throw new IllegalArgumentException("Step not found: " + currentStep);
            }

            Result result = executeWorkflowService.run(currentStep, context);

            currentStep = decideNextStep(workflow, step.name(), result);
        }
    }

    private String decideNextStep(Workflow workflow, String stepName, Result result) {

        var steps = workflow.steps().stream().map(Step::name).toList();
        int index = steps.indexOf(stepName);

        if (result.statusCode() >= 200 && result.statusCode() < 300) {
            return index < steps.size() - 1 ? steps.get(index + 1) : null;
        }

        return null;
    }
}
