package br.com.lucaslima.orchestrator.application.ports.out;

import br.com.lucaslima.orchestrator.application.domains.Workflow;

public interface WorkflowRepositoryPort {
    Workflow load(String flowId, String lastExecutedStepName);
}
