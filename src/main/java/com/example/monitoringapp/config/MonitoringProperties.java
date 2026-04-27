package com.example.monitoringapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "monitoring")
public class MonitoringProperties {

    private final Scheduler scheduler = new Scheduler();
    private final Execution execution = new Execution();
    private final Alert alert = new Alert();
    private final Elasticsearch elasticsearch = new Elasticsearch();
    private final Prometheus prometheus = new Prometheus();
    private final Report report = new Report();

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Execution getExecution() {
        return execution;
    }

    public Alert getAlert() {
        return alert;
    }

    public Elasticsearch getElasticsearch() {
        return elasticsearch;
    }

    public Prometheus getPrometheus() {
        return prometheus;
    }

    public Report getReport() {
        return report;
    }

    public static class Scheduler {
        private boolean enabled = true;
        private String cron = "0 * * * * *";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }
    }

    public static class Execution {
        private long defaultTimeoutMs = 5000L;
        private int historyPageSize = 20;

        public long getDefaultTimeoutMs() {
            return defaultTimeoutMs;
        }

        public void setDefaultTimeoutMs(long defaultTimeoutMs) {
            this.defaultTimeoutMs = defaultTimeoutMs;
        }

        public int getHistoryPageSize() {
            return historyPageSize;
        }

        public void setHistoryPageSize(int historyPageSize) {
            this.historyPageSize = historyPageSize;
        }
    }

    public static class Alert {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class Elasticsearch {
        private String baseUrl = "http://localhost:9200";

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    public static class Prometheus {
        private String baseUrl = "http://localhost:9090";

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    public static class Report {
        private String schedule = "0 0 9 * * *";
        private boolean enabled = true;

        public String getSchedule() {
            return schedule;
        }

        public void setSchedule(String schedule) {
            this.schedule = schedule;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}