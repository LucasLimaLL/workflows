package br.com.lucaslima.orchestrator.adapters.web;

import br.com.lucaslima.orchestrator.application.domains.Step;
import br.com.lucaslima.orchestrator.application.domains.StepExecutionResult;
import br.com.lucaslima.orchestrator.application.domains.StepType;
import br.com.lucaslima.orchestrator.application.ports.out.ExecuteStepPort;
import br.com.lucaslima.orchestrator.common.ContextTemplateResolver;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class HttpStepExecutor implements ExecuteStepPort {

    private final WebClient webClient;

    public HttpStepExecutor(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public StepType getType() {
        return StepType.HTTP;
    }

    @Override
    public StepExecutionResult execute(Step step, Map<String, Object> contextMap, boolean isReprocess) {
        String resolvedUrl = ContextTemplateResolver.resolveUrl(
                step.target(),
                contextMap,
                step.pathVariables() != null ? step.pathVariables().vars() : null,
                step.query() != null ? step.query().params() : null
        );
        Map<String, String> resolvedHeaders = ContextTemplateResolver.resolveHeaders(
                step.metadata() != null ? step.metadata().values() : null,
                contextMap
        );
        Object resolvedBody = ContextTemplateResolver.resolveBody(
                step.payload() != null ? step.payload().data() : null,
                contextMap
        );

        HttpMethod httpMethod = HttpMethod.valueOf(step.verb().toUpperCase());
        WebClient.RequestBodySpec requestSpec = webClient.method(httpMethod).uri(resolvedUrl)
                .headers(h -> { if (resolvedHeaders != null) h.setAll(resolvedHeaders); });
        ResponseEntity<Object> response = (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.DELETE)
                ? requestSpec.retrieve().toEntity(Object.class).block()
                : requestSpec.bodyValue(resolvedBody).retrieve().toEntity(Object.class).block();

        int statusCode = response != null ? response.getStatusCodeValue() : 500;
        Object responseBody = response != null ? response.getBody() : null;
        return new StepExecutionResult(step.name(), statusCode, responseBody);
    }
}
