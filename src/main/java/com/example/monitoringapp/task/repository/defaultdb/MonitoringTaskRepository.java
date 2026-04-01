package com.example.monitoringapp.task.repository.defaultdb;

import com.example.monitoringapp.task.domain.MonitoringTask;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MonitoringTaskRepository {

    @Select("""
            select
                task_id,
                task_nm,
                task_type_cd,
                task_cntnt,
                cast(exec_param as text) as exec_param_json,
                cast(success_param as text) as success_param_json,
                schedule_val,
                task_prio,
                active_yn,
                last_exec_dtm,
                last_exec_rslt,
                last_exec_rslt_msg,
                fst_reg_dtm,
                fnl_upt_dtm
            from tb_mon_task_m
            order by task_prio asc, task_id asc
            """)
    List<MonitoringTask> findAll();

    @Select("""
            select
                task_id,
                task_nm,
                task_type_cd,
                task_cntnt,
                cast(exec_param as text) as exec_param_json,
                cast(success_param as text) as success_param_json,
                schedule_val,
                task_prio,
                active_yn,
                last_exec_dtm,
                last_exec_rslt,
                last_exec_rslt_msg,
                fst_reg_dtm,
                fnl_upt_dtm
            from tb_mon_task_m
            where active_yn = 'Y'
            order by task_prio asc, task_id asc
            """)
    List<MonitoringTask> findAllActive();

    @Select("""
            select
                task_id,
                task_nm,
                task_type_cd,
                task_cntnt,
                cast(exec_param as text) as exec_param_json,
                cast(success_param as text) as success_param_json,
                schedule_val,
                task_prio,
                active_yn,
                last_exec_dtm,
                last_exec_rslt,
                last_exec_rslt_msg,
                fst_reg_dtm,
                fnl_upt_dtm
            from tb_mon_task_m
            where task_id = #{taskId}
            """)
    MonitoringTask findById(@Param("taskId") Long taskId);

    @Select("select coalesce(max(task_id), 0) + 1 from tb_mon_task_m")
    Long nextTaskId();

    @Insert("""
            insert into tb_mon_task_m (
                task_id,
                task_nm,
                task_type_cd,
                task_cntnt,
                exec_param,
                success_param,
                schedule_val,
                task_prio,
                active_yn,
                fst_reg_dtm,
                fnl_upt_dtm
            ) values (
                #{task.taskId},
                #{task.taskNm},
                #{task.taskTypeCd},
                #{task.taskCntnt},
                cast(#{task.execParamJson} as jsonb),
                cast(#{task.successParamJson} as jsonb),
                #{task.scheduleVal},
                #{task.taskPrio},
                #{task.activeYn},
                #{task.fstRegDtm},
                #{task.fnlUptDtm}
            )
            """)
    int insert(@Param("task") MonitoringTask task);

    @Update("""
            update tb_mon_task_m
            set
                task_nm = #{task.taskNm},
                task_type_cd = #{task.taskTypeCd},
                task_cntnt = #{task.taskCntnt},
                exec_param = cast(#{task.execParamJson} as jsonb),
                success_param = cast(#{task.successParamJson} as jsonb),
                schedule_val = #{task.scheduleVal},
                task_prio = #{task.taskPrio},
                active_yn = #{task.activeYn},
                fnl_upt_dtm = #{task.fnlUptDtm}
            where task_id = #{task.taskId}
            """)
    int update(@Param("task") MonitoringTask task);

    @Update("""
            update tb_mon_task_m
            set
                active_yn = 'N',
                fnl_upt_dtm = #{updatedAt}
            where task_id = #{taskId}
            """)
    int deactivate(@Param("taskId") Long taskId, @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            update tb_mon_task_m
            set
                active_yn = 'Y',
                fnl_upt_dtm = #{updatedAt}
            where task_id = #{taskId}
            """)
    int activate(@Param("taskId") Long taskId, @Param("updatedAt") LocalDateTime updatedAt);

    @Update("""
            update tb_mon_task_m
            set
                last_exec_dtm = #{executedAt},
                last_exec_rslt = #{lastExecRslt},
                last_exec_rslt_msg = #{lastExecRsltMsg},
                fnl_upt_dtm = #{updatedAt}
            where task_id = #{taskId}
            """)
    int updateExecutionSummary(
            @Param("taskId") Long taskId,
            @Param("executedAt") LocalDateTime executedAt,
            @Param("lastExecRslt") String lastExecRslt,
            @Param("lastExecRsltMsg") String lastExecRsltMsg,
            @Param("updatedAt") LocalDateTime updatedAt
    );
}