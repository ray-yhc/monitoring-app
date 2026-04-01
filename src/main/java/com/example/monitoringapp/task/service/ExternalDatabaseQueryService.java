package com.example.monitoringapp.task.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ExternalDatabaseQueryService {

    private final Map<String, JdbcTemplate> jdbcTemplates;

    public ExternalDatabaseQueryService(
            @Qualifier("defaultDataSource") DataSource defaultDataSource,
            @Qualifier("moduleADataSource") DataSource moduleADataSource,
            @Qualifier("moduleBDataSource") DataSource moduleBDataSource,
            @Qualifier("moduleCDataSource") DataSource moduleCDataSource
    ) {
        this.jdbcTemplates = new LinkedHashMap<>();
        this.jdbcTemplates.put("default", new JdbcTemplate(defaultDataSource));
        this.jdbcTemplates.put("module-a", new JdbcTemplate(moduleADataSource));
        this.jdbcTemplates.put("module-b", new JdbcTemplate(moduleBDataSource));
        this.jdbcTemplates.put("module-c", new JdbcTemplate(moduleCDataSource));
    }

    public Object queryScalar(String datasourceKey, String query) {
        JdbcTemplate jdbcTemplate = jdbcTemplates.get(datasourceKey);
        if (jdbcTemplate == null) {
            throw new IllegalArgumentException("Unknown datasource key: " + datasourceKey);
        }
        return jdbcTemplate.queryForObject(query, Object.class);
    }
}