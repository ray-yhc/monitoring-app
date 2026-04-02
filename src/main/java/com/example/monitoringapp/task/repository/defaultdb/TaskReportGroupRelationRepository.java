package com.example.monitoringapp.task.repository.defaultdb;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskReportGroupRelationRepository {

    @Select("""
            select report_group_id
            from tb_mon_task_report_group_r
            where task_id = #{taskId}
            order by report_group_id asc
            """)
    List<Long> findReportGroupIdsByTaskId(@Param("taskId") Long taskId);

    @Delete("delete from tb_mon_task_report_group_r where task_id = #{taskId}")
    int deleteByTaskId(@Param("taskId") Long taskId);

    @Insert("""
            insert into tb_mon_task_report_group_r (
                task_id,
                report_group_id,
                fst_reg_dtm,
                fnl_upt_dtm
            ) values (
                #{taskId},
                #{reportGroupId},
                #{createdAt},
                #{updatedAt}
            )
            """)
    int insert(
            @Param("taskId") Long taskId,
            @Param("reportGroupId") Long reportGroupId,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("updatedAt") LocalDateTime updatedAt
    );
}