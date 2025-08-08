package br.com.lucaslima.orchestrator.adapters.messaging;

import br.com.lucaslima.orchestrator.application.domains.Step;
import br.com.lucaslima.orchestrator.application.domains.StepExecutionResult;
import br.com.lucaslima.orchestrator.application.domains.StepType;
import br.com.lucaslima.orchestrator.application.ports.out.ExecuteStepPort;
import br.com.lucaslima.orchestrator.common.ContextTemplateResolver;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Component
public class SqsStepExecutor implements ExecuteStepPort {

    private final SqsClient sqsClient;

    public SqsStepExecutor(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @Override
    public StepType getType() {
        return StepType.SQS;
    }

    @Override
    public StepExecutionResult execute(Step step, Map<String, Object> contextMap, boolean isReprocess) {
        String role = step.metadata() != null ? step.metadata().values().get("role") : null;
        if (isReprocess && "fallback".equalsIgnoreCase(role)) {
            return new StepExecutionResult(step.name(), 204, Map.of("skipped", true, "reason", "reprocess-fallback-skip"));
        }

        String queueUrl = ContextTemplateResolver.resolveString(step.target(), contextMap);
        Object messageObj = ContextTemplateResolver.resolveBody(
                step.payload() != null ? step.payload().data() : null,
                contextMap
        );
        String messageBodyJson = ContextTemplateResolver.toJson(messageObj);

        Map<String, MessageAttributeValue> messageAttributes = step.metadata() == null
                ? new HashMap<>()
                : step.metadata().values().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(ContextTemplateResolver.resolveString(e.getValue(), contextMap))
                        .build()
        ));

        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBodyJson)
                .messageAttributes(messageAttributes)
                .build();

        SendMessageResponse response = sqsClient.sendMessage(request);
        return new StepExecutionResult(step.name(), 200, response.messageId());
    }
}
