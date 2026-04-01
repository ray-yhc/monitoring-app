package com.example.monitoringapp.task.repository.moduleb;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ModuleBDatabaseMapper {

    @Select("SELECT current_database()")
    String currentDatabase();

    @Select("SELECT current_schema()")
    String currentSchema();
}
