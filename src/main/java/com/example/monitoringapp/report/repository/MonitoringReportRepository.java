package com.example.monitoringapp.report.repository;

import com.example.monitoringapp.report.domain.MonitoringReport;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MonitoringReportRepository {

    @Select("select coalesce(max(report_id), 0) + 1 from tb_mon_report_l")
    Long nextReportId();

    @Insert("""
            insert into tb_mon_report_l (
                report_id,
                report_dt,
                analysis_start_dtm,
                analysis_end_dtm,
                total_exec_cnt,
                success_cnt,
                failure_cnt,
                error_cnt,
                exec_summary,
                ai_advice,
                report_status,
                error_msg,
                fst_reg_dtm,
                fnl_upt_dtm
            ) values (
                #{report.reportId},
                #{report.reportDt},
                #{report.analysisStartDtm},
                #{report.analysisEndDtm},
                #{report.totalExecCnt},
                #{report.successCnt},
                #{report.failureCnt},
                #{report.errorCnt},
                #{report.execSummary},
                #{report.aiAdvice},
                #{report.reportStatus},
                #{report.errorMsg},
                #{report.fstRegDtm},
                #{report.fnlUptDtm}
            )
            """)
    int insert(@Param("report") MonitoringReport report);

    @Update("""
            update tb_mon_report_l set
                total_exec_cnt    = #{report.totalExecCnt},
                success_cnt       = #{report.successCnt},
                failure_cnt       = #{report.failureCnt},
                error_cnt         = #{report.errorCnt},
                exec_summary      = #{report.execSummary},
                ai_advice         = #{report.aiAdvice},
                report_status     = #{report.reportStatus},
                error_msg         = #{report.errorMsg},
                fnl_upt_dtm       = #{report.fnlUptDtm}
            where report_id = #{report.reportId}
            """)
    int update(@Param("report") MonitoringReport report);

    @Select("""
            select
                report_id,
                report_dt,
                analysis_start_dtm,
                analysis_end_dtm,
                total_exec_cnt,
                success_cnt,
                failure_cnt,
                error_cnt,
                exec_summary,
                ai_advice,
                report_status,
                error_msg,
                fst_reg_dtm,
                fnl_upt_dtm
            from tb_mon_report_l
            where report_id = #{reportId}
            """)
    MonitoringReport findById(@Param("reportId") Long reportId);

    @Select("""
            select
                report_id,
                report_dt,
                analysis_start_dtm,
                analysis_end_dtm,
                total_exec_cnt,
                success_cnt,
                failure_cnt,
                error_cnt,
                exec_summary,
                ai_advice,
                report_status,
                error_msg,
                fst_reg_dtm,
                fnl_upt_dtm
            from tb_mon_report_l
            order by report_dt desc
            limit #{limit} offset #{offset}
            """)
    List<MonitoringReport> findAll(@Param("limit") int limit, @Param("offset") int offset);

    @Select("select count(*) from tb_mon_report_l")
    long countAll();

    @Select("""
            select
                report_id,
                report_dt,
                analysis_start_dtm,
                analysis_end_dtm,
                total_exec_cnt,
                success_cnt,
                failure_cnt,
                error_cnt,
                exec_summary,
                ai_advice,
                report_status,
                error_msg,
                fst_reg_dtm,
                fnl_upt_dtm
            from tb_mon_report_l
            order by report_dt desc
            limit 1
            """)
    MonitoringReport findLatest();

    @Select("""
            select
                report_id,
                report_dt,
                analysis_start_dtm,
                analysis_end_dtm,
                total_exec_cnt,
                success_cnt,
                failure_cnt,
                error_cnt,
                exec_summary,
                ai_advice,
                report_status,
                error_msg,
                fst_reg_dtm,
                fnl_upt_dtm
            from tb_mon_report_l
            where report_dt = #{reportDt}
            limit 1
            """)
    MonitoringReport findByReportDt(@Param("reportDt") LocalDate reportDt);
}
