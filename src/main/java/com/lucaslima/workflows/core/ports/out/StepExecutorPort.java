package com.lucaslima.workflows.core.ports.out;

import com.lucaslima.workflows.core.domains.Result;
import com.lucaslima.workflows.core.domains.Step;
import com.lucaslima.workflows.core.domains.Type;

import java.util.Map;

public interface StepExecutorPort {

    Type getType();

    Result execute(Step step, Map<String, Object> context);
}
