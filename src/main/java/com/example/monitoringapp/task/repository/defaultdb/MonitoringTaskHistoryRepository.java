package com.example.monitoringapp.task.repository.defaultdb;

import com.example.monitoringapp.task.domain.MonitoringTaskHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MonitoringTaskHistoryRepository {

    @Select("select coalesce(max(task_hist_id), 0) + 1 from tb_mon_task_hist_m")
    Long nextHistoryId();

    @Insert("""
            insert into tb_mon_task_hist_m (
                task_hist_id,
                task_id,
                exec_dtm,
                exec_trg_type_cd,
                exec_rslt,
                exec_rslt_msg,
                exec_rslt_data,
                alert_event_type,
                exec_dur_ms,
                fst_reg_dtm,
                fnl_upt_dtm
            ) values (
                #{history.taskHistId},
                #{history.taskId},
                #{history.execDtm},
                #{history.execTrgTypeCd},
                #{history.execRslt},
                #{history.execRsltMsg},
                cast(#{history.execRsltDataJson} as jsonb),
                #{history.alertEventType},
                #{history.execDurMs},
                #{history.fstRegDtm},
                #{history.fnlUptDtm}
            )
            """)
    int insert(@Param("history") MonitoringTaskHistory history);

    @Select("""
            select
                task_hist_id,
                task_id,
                exec_dtm,
                exec_trg_type_cd,
                exec_rslt,
                exec_rslt_msg,
                cast(exec_rslt_data as text) as exec_rslt_data_json,
                alert_event_type,
                exec_dur_ms,
                fst_reg_dtm,
                fnl_upt_dtm
            from tb_mon_task_hist_m
            where task_id = #{taskId}
            order by exec_dtm desc
            limit #{limit} offset #{offset}
            """)
    List<MonitoringTaskHistory> findByTaskId(
            @Param("taskId") Long taskId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Select("select count(*) from tb_mon_task_hist_m where task_id = #{taskId}")
    long countByTaskId(@Param("taskId") Long taskId);
}