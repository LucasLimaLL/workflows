package br.com.lucaslima.orchestrator.adapters.persistence;

import br.com.lucaslima.orchestrator.application.domains.Step;
import br.com.lucaslima.orchestrator.application.domains.Workflow;
import br.com.lucaslima.orchestrator.application.ports.out.WorkflowRepositoryPort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

@Component
public class DynamoDbWorkflowRepositoryAdapter implements WorkflowRepositoryPort {

    private final DynamoDbClient dynamoDbClient;
    private final ObjectMapper objectMapper;
    private final String tableName;

    public DynamoDbWorkflowRepositoryAdapter(DynamoDbClient dynamoDbClient,
                                             ObjectMapper objectMapper,
                                             @Value("${orchestrator.workflows.table-name}") String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.objectMapper = objectMapper;
        this.tableName = tableName;
    }

    @Override
    public Workflow load(String flowId, String lastExecutedStepName) {
        Workflow fullWorkflow = loadFromDynamoDb(flowId);

        if (lastExecutedStepName == null || lastExecutedStepName.isBlank()) {
            return fullWorkflow;
        }

        List<Step> steps = fullWorkflow.steps();
        int index = IntStream.range(0, steps.size())
                .filter(i -> steps.get(i).name().equals(lastExecutedStepName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("lastStep não encontrado: " + lastExecutedStepName));

        return (index + 1 >= steps.size())
                ? new Workflow(fullWorkflow.name(), fullWorkflow.version(), List.of(), fullWorkflow.errorStep())
                : new Workflow(fullWorkflow.name(), fullWorkflow.version(), steps.subList(index + 1, steps.size()), fullWorkflow.errorStep());
    }

    private Workflow loadFromDynamoDb(String flowId) {
        Map<String, AttributeValue> key = Map.of("flowId", AttributeValue.builder().s(flowId).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(this.tableName)
                .key(key)
                .consistentRead(Boolean.TRUE)
                .build();

        Map<String, AttributeValue> item = dynamoDbClient.getItem(request).item();
        if (item == null || !item.containsKey("definition")) {
            throw new IllegalArgumentException("Workflow não encontrado no DynamoDB: " + flowId);
        }

        String workflowJson = item.get("definition").s();
        try {
            return objectMapper.readValue(workflowJson, new TypeReference<Workflow>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao desserializar workflow do DynamoDB: " + e.getMessage(), e);
        }
    }
}
