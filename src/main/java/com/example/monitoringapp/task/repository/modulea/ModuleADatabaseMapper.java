package com.example.monitoringapp.task.repository.modulea;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ModuleADatabaseMapper {

    @Select("SELECT current_database()")
    String currentDatabase();

    @Select("SELECT current_schema()")
    String currentSchema();
}
