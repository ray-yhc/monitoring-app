package com.example.monitoringapp.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

public abstract class AbstractMyBatisDataSourceConfig {

    protected DataSource buildDataSource(DataSourceProperties properties, String poolName) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        dataSource.setPoolName(poolName);
        dataSource.setInitializationFailTimeout(-1);
        return dataSource;
    }

    protected SqlSessionFactory buildSqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);

        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        sessionFactoryBean.setConfiguration(configuration);

        return Objects.requireNonNull(sessionFactoryBean.getObject(), "SqlSessionFactory must not be null");
    }

    protected DataSourceTransactionManager buildTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
