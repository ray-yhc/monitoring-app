package com.example.monitoringapp.task.service;

import com.example.monitoringapp.common.json.JsonSupport;
import com.example.monitoringapp.config.MonitoringProperties;
import com.example.monitoringapp.task.domain.MonitoringTask;
import com.example.monitoringapp.task.domain.MonitoringTaskHistory;
import com.example.monitoringapp.task.domain.ReportGroup;
import com.example.monitoringapp.task.repository.defaultdb.MonitoringTaskHistoryRepository;
import com.example.monitoringapp.task.repository.defaultdb.MonitoringTaskRepository;
import com.example.monitoringapp.task.repository.defaultdb.ReportGroupRepository;
import com.example.monitoringapp.task.repository.defaultdb.TaskReportGroupRelationRepository;
import com.example.monitoringapp.task.service.dto.MonitoringTaskHistoryPageResponse;
import com.example.monitoringapp.task.service.dto.MonitoringTaskHistoryResponse;
import com.example.monitoringapp.task.service.dto.MonitoringTaskResponse;
import com.example.monitoringapp.task.service.dto.ReportGroupResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MonitoringTaskQueryService {

    private final MonitoringTaskRepository monitoringTaskRepository;
    private final MonitoringTaskHistoryRepository monitoringTaskHistoryRepository;
    private final TaskReportGroupRelationRepository taskReportGroupRelationRepository;
    private final ReportGroupRepository reportGroupRepository;
    private final JsonSupport jsonSupport;
    private final MonitoringProperties monitoringProperties;

    public MonitoringTaskQueryService(
            MonitoringTaskRepository monitoringTaskRepository,
            MonitoringTaskHistoryRepository monitoringTaskHistoryRepository,
            TaskReportGroupRelationRepository taskReportGroupRelationRepository,
            ReportGroupRepository reportGroupRepository,
            JsonSupport jsonSupport,
            MonitoringProperties monitoringProperties
    ) {
        this.monitoringTaskRepository = monitoringTaskRepository;
        this.monitoringTaskHistoryRepository = monitoringTaskHistoryRepository;
        this.taskReportGroupRelationRepository = taskReportGroupRelationRepository;
        this.reportGroupRepository = reportGroupRepository;
        this.jsonSupport = jsonSupport;
        this.monitoringProperties = monitoringProperties;
    }

    public List<MonitoringTaskResponse> getTasks() {
        return monitoringTaskRepository.findAll().stream().map(task -> toResponse(task, false)).toList();
    }

    public MonitoringTaskResponse getTask(Long taskId) {
        return toResponse(getTaskDomain(taskId), true);
    }

    public MonitoringTask getTaskDomain(Long taskId) {
        MonitoringTask task = monitoringTaskRepository.findById(taskId);
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found: " + taskId);
        }
        return task;
    }

    public MonitoringTaskHistoryPageResponse getTaskHistories(Long taskId, Integer page, Integer size) {
        getTaskDomain(taskId);
        int safePage = page == null || page < 0 ? 0 : page;
        int safeSize = size == null || size <= 0 ? monitoringProperties.getExecution().getHistoryPageSize() : size;
        int offset = safePage * safeSize;

        List<MonitoringTaskHistoryResponse> histories = monitoringTaskHistoryRepository.findByTaskId(taskId, safeSize, offset)
                .stream()
                .map(this::toHistoryResponse)
                .toList();

        MonitoringTaskHistoryPageResponse response = new MonitoringTaskHistoryPageResponse();
        response.setTaskId(taskId);
        response.setPage(safePage);
        response.setSize(safeSize);
        response.setTotalCount(monitoringTaskHistoryRepository.countByTaskId(taskId));
        response.setContents(histories);
        return response;
    }

    private MonitoringTaskResponse toResponse(MonitoringTask task, boolean includeReportGroups) {
        MonitoringTaskResponse response = new MonitoringTaskResponse();
        response.setTaskId(task.getTaskId());
        response.setTaskNm(task.getTaskNm());
        response.setTaskTypeCd(task.getTaskTypeCd());
        response.setTaskCntnt(task.getTaskCntnt());
        response.setExecParam(jsonSupport.readTree(task.getExecParamJson()));
        response.setSuccessParam(jsonSupport.readTree(task.getSuccessParamJson()));
        response.setScheduleVal(task.getScheduleVal());
        response.setTaskPrio(task.getTaskPrio());
        response.setActiveYn(task.getActiveYn());
        response.setLastExecDtm(task.getLastExecDtm());
        response.setLastExecRslt(task.getLastExecRslt());
        response.setLastExecRsltMsg(task.getLastExecRsltMsg());

        if (includeReportGroups) {
            List<Long> reportGroupIds = taskReportGroupRelationRepository.findReportGroupIdsByTaskId(task.getTaskId());
            response.setReportGroupIds(reportGroupIds);
            response.setReportGroups(findReportGroupsByIds(reportGroupIds));
        }

        return response;
    }

    private List<ReportGroupResponse> findReportGroupsByIds(List<Long> reportGroupIds) {
        if (reportGroupIds == null || reportGroupIds.isEmpty()) {
            return List.of();
        }

        List<ReportGroup> reportGroups = reportGroupRepository.findByIds(reportGroupIds);
        Map<Long, ReportGroupResponse> reportGroupMap = new LinkedHashMap<>();
        for (ReportGroup reportGroup : reportGroups) {
            reportGroupMap.put(reportGroup.getReportGroupId(), toReportGroupResponse(reportGroup));
        }

        return reportGroupIds.stream()
                .map(reportGroupMap::get)
                .filter(item -> item != null)
                .toList();
    }

    private ReportGroupResponse toReportGroupResponse(ReportGroup reportGroup) {
        ReportGroupResponse response = new ReportGroupResponse();
        response.setReportGroupId(reportGroup.getReportGroupId());
        response.setReportGroupName(reportGroup.getReportGroupName());
        response.setDescription(reportGroup.getDescription());
        response.setChatRoomId(reportGroup.getChatRoomId());
        response.setSendYn(reportGroup.getSendYn());
        return response;
    }

    private MonitoringTaskHistoryResponse toHistoryResponse(MonitoringTaskHistory history) {
        MonitoringTaskHistoryResponse response = new MonitoringTaskHistoryResponse();
        response.setTaskHistId(history.getTaskHistId());
        response.setTaskId(history.getTaskId());
        response.setExecDtm(history.getExecDtm());
        response.setExecTrgTypeCd(history.getExecTrgTypeCd());
        response.setExecRslt(history.getExecRslt());
        response.setExecRsltMsg(history.getExecRsltMsg());
        response.setExecRsltData(history.getExecRsltDataJson() == null ? null : jsonSupport.readTree(history.getExecRsltDataJson()));
        response.setAlertEventType(history.getAlertEventType());
        response.setExecDurMs(history.getExecDurMs());
        return response;
    }
}