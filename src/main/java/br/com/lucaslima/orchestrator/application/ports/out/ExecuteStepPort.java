package br.com.lucaslima.orchestrator.application.ports.out;

import br.com.lucaslima.orchestrator.application.domains.Step;
import br.com.lucaslima.orchestrator.application.domains.StepExecutionResult;
import br.com.lucaslima.orchestrator.application.domains.StepType;
import java.util.Map;

public interface ExecuteStepPort {
    StepType getType();
    StepExecutionResult execute(Step step, Map<String, Object> contextMap, boolean isReprocess);
}
