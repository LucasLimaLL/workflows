package br.com.lucaslima.orchestrator.application.ports.in;

import java.util.Map;

public interface OrchestrationUseCase {
    void run(String flowId, Map<String, Object> initialContextMap, boolean isReprocess, String lastExecutedStepName);
}
