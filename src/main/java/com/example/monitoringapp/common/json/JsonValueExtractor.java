package com.example.monitoringapp.common.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class JsonValueExtractor {

    public JsonNode extract(JsonNode root, String valuePath) {
        if (root == null || valuePath == null || valuePath.isBlank()) {
            return null;
        }

        JsonNode current = root;
        String[] tokens = valuePath.split("\\.");
        for (String token : tokens) {
            current = extractToken(current, token);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private JsonNode extractToken(JsonNode current, String token) {
        if (current == null || token == null || token.isBlank()) {
            return null;
        }

        String remaining = token;
        int bracketIndex = remaining.indexOf('[');
        if (bracketIndex < 0) {
            return current.get(remaining);
        }

        String fieldName = remaining.substring(0, bracketIndex);
        if (!fieldName.isBlank()) {
            current = current.get(fieldName);
        }

        while (current != null && bracketIndex >= 0) {
            int endBracketIndex = remaining.indexOf(']', bracketIndex);
            if (endBracketIndex < 0) {
                return null;
            }
            String indexToken = remaining.substring(bracketIndex + 1, endBracketIndex);
            int index = Integer.parseInt(indexToken);
            current = current.get(index);
            bracketIndex = remaining.indexOf('[', endBracketIndex);
        }
        return current;
    }
}