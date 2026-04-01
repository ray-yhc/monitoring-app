package com.example.monitoringapp.task.executor;

import com.example.monitoringapp.common.json.JsonSupport;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractHttpTaskExecutor implements TaskExecutor {

    private final WebClient.Builder webClientBuilder;
    protected final JsonSupport jsonSupport;
    private final long defaultTimeoutMs;

    protected AbstractHttpTaskExecutor(WebClient.Builder webClientBuilder, JsonSupport jsonSupport, long defaultTimeoutMs) {
        this.webClientBuilder = webClientBuilder;
        this.jsonSupport = jsonSupport;
        this.defaultTimeoutMs = defaultTimeoutMs;
    }

    protected HttpCallResult executeHttp(JsonNode execParam) {
        String url = requiredText(execParam, "url");
        HttpMethod httpMethod = HttpMethod.valueOf(execParam.path("method").asText("GET"));
        long timeoutMs = execParam.path("timeoutMs").asLong(defaultTimeoutMs);
        URI uri = buildUri(url, execParam.path("queryParams"));

        WebClient.RequestBodyUriSpec requestBodyUriSpec = webClientBuilder.build().method(httpMethod);
        WebClient.RequestHeadersSpec<?> requestHeadersSpec;

        if (execParam.has("body") && httpMethod != HttpMethod.GET) {
            Object requestBody = jsonSupport.getObjectMapper().convertValue(execParam.get("body"), Object.class);
            requestHeadersSpec = requestBodyUriSpec.uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> applyHeaders(headers, execParam.path("headers")))
                    .bodyValue(requestBody);
        } else {
            requestHeadersSpec = requestBodyUriSpec.uri(uri)
                    .headers(headers -> applyHeaders(headers, execParam.path("headers")));
        }

        Instant start = Instant.now();
        HttpCallResult result = requestHeadersSpec
                .exchangeToMono(response -> response.bodyToMono(String.class)
                        .defaultIfEmpty("")
                        .map(body -> new HttpCallResult(response.statusCode().value(), body, 0L)))
                .timeout(Duration.ofMillis(timeoutMs))
                .block();

        if (result == null) {
            throw new IllegalStateException("No response from HTTP call");
        }
        long durationMs = Duration.between(start, Instant.now()).toMillis();
        return new HttpCallResult(result.statusCode(), result.body(), durationMs);
    }

    protected JsonNode readBodyAsJson(String body) {
        return jsonSupport.readTree(body == null || body.isBlank() ? "{}" : body);
    }

    private URI buildUri(String url, JsonNode queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        if (queryParams != null && queryParams.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = queryParams.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                builder.queryParam(entry.getKey(), entry.getValue().asText());
            }
        }
        return builder.build(true).toUri();
    }

    private void applyHeaders(org.springframework.http.HttpHeaders headers, JsonNode headerNode) {
        if (headerNode == null || !headerNode.isObject()) {
            return;
        }
        Iterator<Map.Entry<String, JsonNode>> fields = headerNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            headers.add(entry.getKey(), entry.getValue().asText());
        }
    }

    protected String requiredText(JsonNode jsonNode, String fieldName) {
        if (jsonNode == null || jsonNode.get(fieldName) == null || jsonNode.get(fieldName).asText().isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return jsonNode.get(fieldName).asText();
    }
}