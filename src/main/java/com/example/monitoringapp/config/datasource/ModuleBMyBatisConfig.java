package com.example.monitoringapp.config.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Profile("local")
@Configuration
@MapperScan(
        basePackages = "com.example.monitoringapp.task.repository.moduleb",
        sqlSessionFactoryRef = "moduleBSqlSessionFactory",
        sqlSessionTemplateRef = "moduleBSqlSessionTemplate"
)
public class ModuleBMyBatisConfig extends AbstractMyBatisDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.module-b")
    public DataSource moduleBDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public SqlSessionFactory moduleBSqlSessionFactory(
            @Qualifier("moduleBDataSource") DataSource dataSource
    ) throws Exception {
        return buildSqlSessionFactory(dataSource);
    }

    @Bean
    public SqlSessionTemplate moduleBSqlSessionTemplate(
            @Qualifier("moduleBSqlSessionFactory") SqlSessionFactory sqlSessionFactory
    ) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public DataSourceTransactionManager moduleBTransactionManager(
            @Qualifier("moduleBDataSource") DataSource dataSource
    ) {
        return buildTransactionManager(dataSource);
    }
}
