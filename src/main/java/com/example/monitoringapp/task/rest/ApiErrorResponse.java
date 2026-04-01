package com.example.monitoringapp.task.rest;

import java.util.ArrayList;
import java.util.List;

public class ApiErrorResponse {

    private String code;
    private String message;
    private final List<FieldErrorItem> fieldErrors = new ArrayList<>();

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<FieldErrorItem> getFieldErrors() { return fieldErrors; }
    public void addFieldError(String field, String reason) { this.fieldErrors.add(new FieldErrorItem(field, reason)); }

    public static class FieldErrorItem {
        private final String field;
        private final String reason;

        public FieldErrorItem(String field, String reason) {
            this.field = field;
            this.reason = reason;
        }

        public String getField() { return field; }
        public String getReason() { return reason; }
    }
}