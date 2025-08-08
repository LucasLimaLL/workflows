package br.com.lucaslima.orchestrator.adapters.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Component
public class HttpLoggingExchangeFilter implements ExchangeFilterFunction {

    private static final Logger LOG = LoggerFactory.getLogger("HTTP_JSON");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        long startNanos = System.nanoTime();
        logLine("request", request.method().name(), request.url().toString(), request.headers(), "stream", null, null);

        return next.exchange(request).flatMap(response ->
                response.bodyToMono(String.class).defaultIfEmpty("")
                        .flatMap(body -> {
                            long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000;
                            logLine("response", request.method().name(), request.url().toString(), request.headers(), body, response.rawStatusCode(), elapsedMs);
                            return Mono.just(ClientResponse.create(response.statusCode())
                                    .headers(h -> h.addAll(response.headers().asHttpHeaders()))
                                    .body(body).build());
                        })
        ).onErrorResume(error -> {
            long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000;
            logLine("error", request.method().name(), request.url().toString(), request.headers(), String.valueOf(error), 500, elapsedMs);
            return Mono.error(error);
        });
    }

    private void logLine(String phase, String method, String url, HttpHeaders headers, Object body, Integer status, Long durationMs) {
        try {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("phase", phase).put("method", method).put("url", url);
            ObjectNode headerNode = OBJECT_MAPPER.valueToTree(headers);
            if (headerNode.has("Authorization")) headerNode.put("Authorization", "***masked***");
            node.set("headers", headerNode);
            node.set("body", OBJECT_MAPPER.valueToTree(body));
            if (status != null) node.put("status", status);
            if (durationMs != null) node.put("durationMs", durationMs);
            LOG.info(OBJECT_MAPPER.writeValueAsString(node));
        } catch (Exception ignored) {}
    }
}
