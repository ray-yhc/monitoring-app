package com.example.monitoringapp.task.service;

import com.example.monitoringapp.task.domain.ComparisonType;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

@Component
public class ComparisonEvaluator {

    public boolean evaluate(Object actualValue, ComparisonType comparisonType, JsonNode expectedValueNode) {
        return switch (comparisonType) {
            case EQUALS -> Objects.equals(normalize(actualValue), normalize(expectedValueNode));
            case NOT_EQUALS -> !Objects.equals(normalize(actualValue), normalize(expectedValueNode));
            case GREATER_THAN -> numeric(actualValue).compareTo(numeric(expectedValueNode)) > 0;
            case GREATER_THAN_OR_EQUAL -> numeric(actualValue).compareTo(numeric(expectedValueNode)) >= 0;
            case LESS_THAN -> numeric(actualValue).compareTo(numeric(expectedValueNode)) < 0;
            case LESS_THAN_OR_EQUAL -> numeric(actualValue).compareTo(numeric(expectedValueNode)) <= 0;
            case CONTAINS -> String.valueOf(actualValue).contains(String.valueOf(normalize(expectedValueNode)));
        };
    }

    private Object normalize(Object value) {
        if (value instanceof JsonNode jsonNode) {
            if (jsonNode.isNumber()) {
                return jsonNode.decimalValue();
            }
            if (jsonNode.isBoolean()) {
                return jsonNode.booleanValue();
            }
            if (jsonNode.isNull()) {
                return null;
            }
            if (jsonNode.isTextual()) {
                return jsonNode.textValue();
            }
            return jsonNode.toString();
        }
        if (value instanceof Number number) {
            return new BigDecimal(number.toString());
        }
        return value;
    }

    private BigDecimal numeric(Object value) {
        Object normalized = normalize(value);
        if (normalized == null) {
            throw new IllegalArgumentException("Numeric comparison requires non-null value");
        }
        if (normalized instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        return new BigDecimal(String.valueOf(normalized));
    }
}