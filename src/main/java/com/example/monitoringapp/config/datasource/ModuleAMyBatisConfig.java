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
        basePackages = "com.example.monitoringapp.task.repository.modulea",
        sqlSessionFactoryRef = "moduleASqlSessionFactory",
        sqlSessionTemplateRef = "moduleASqlSessionTemplate"
)
public class ModuleAMyBatisConfig extends AbstractMyBatisDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.module-a")
    public DataSource moduleADataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public SqlSessionFactory moduleASqlSessionFactory(
            @Qualifier("moduleADataSource") DataSource dataSource
    ) throws Exception {
        return buildSqlSessionFactory(dataSource);
    }

    @Bean
    public SqlSessionTemplate moduleASqlSessionTemplate(
            @Qualifier("moduleASqlSessionFactory") SqlSessionFactory sqlSessionFactory
    ) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public DataSourceTransactionManager moduleATransactionManager(
            @Qualifier("moduleADataSource") DataSource dataSource
    ) {
        return buildTransactionManager(dataSource);
    }
}
