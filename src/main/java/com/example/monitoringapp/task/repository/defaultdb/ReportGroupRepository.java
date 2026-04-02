package com.example.monitoringapp.task.repository.defaultdb;

import com.example.monitoringapp.task.domain.ReportGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReportGroupRepository {

    @Select("""
            select
                report_group_id,
                report_group_name,
                description,
                chat_room_id,
                send_yn,
                fst_reg_dtm,
                fnl_upt_dtm
            from tb_mon_report_group_m
            order by report_group_name asc, report_group_id asc
            """)
    List<ReportGroup> findAll();

    @Select("""
            select
                report_group_id,
                report_group_name,
                description,
                chat_room_id,
                send_yn,
                fst_reg_dtm,
                fnl_upt_dtm
            from tb_mon_report_group_m
            where report_group_id = #{reportGroupId}
            """)
    ReportGroup findById(@Param("reportGroupId") Long reportGroupId);

    @Select("""
            <script>
            select
                report_group_id,
                report_group_name,
                description,
                chat_room_id,
                send_yn,
                fst_reg_dtm,
                fnl_upt_dtm
            from tb_mon_report_group_m
            where report_group_id in
            <foreach item='id' collection='reportGroupIds' open='(' separator=',' close=')'>
                #{id}
            </foreach>
            </script>
            """)
    List<ReportGroup> findByIds(@Param("reportGroupIds") List<Long> reportGroupIds);
}