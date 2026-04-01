package com.example.monitoringapp.config.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Profile("local")
@Configuration
@MapperScan(
        basePackages = "com.example.monitoringapp.task.repository.temp",
        sqlSessionFactoryRef = "defaultSqlSessionFactory",
        sqlSessionTemplateRef = "defaultSqlSessionTemplate"
)
public class DefaultMyBatisConfig extends AbstractMyBatisDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.default")
    public DataSource defaultDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public SqlSessionFactory defaultSqlSessionFactory(
            @Qualifier("defaultDataSource") DataSource dataSource
    ) throws Exception {
        return buildSqlSessionFactory(dataSource);
    }

    @Bean
    @Primary
    public SqlSessionTemplate defaultSqlSessionTemplate(
            @Qualifier("defaultSqlSessionFactory") SqlSessionFactory sqlSessionFactory
    ) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    @Primary
    public DataSourceTransactionManager defaultTransactionManager(
            @Qualifier("defaultDataSource") DataSource dataSource
    ) {
        return buildTransactionManager(dataSource);
    }
}
