package com.lucaslima.workflows.core.ports.out;

import com.lucaslima.workflows.core.domains.Workflow;

import java.util.Optional;

public interface SearchWorkflowPort {

    Optional<Workflow> findByNameAndVersion(String name, String version);

    Optional<Workflow> findByName(String name);
}
