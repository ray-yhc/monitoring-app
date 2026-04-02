package com.example.monitoringapp.task.service;

import com.example.monitoringapp.common.json.JsonSupport;
import com.example.monitoringapp.task.domain.MonitoringTask;
import com.example.monitoringapp.task.repository.defaultdb.MonitoringTaskRepository;
import com.example.monitoringapp.task.repository.defaultdb.TaskReportGroupRelationRepository;
import com.example.monitoringapp.task.service.dto.MonitoringTaskResponse;
import com.example.monitoringapp.task.service.dto.MonitoringTaskUpsertRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MonitoringTaskCommandService {

    private final MonitoringTaskRepository monitoringTaskRepository;
    private final TaskReportGroupRelationRepository taskReportGroupRelationRepository;
    private final MonitoringTaskQueryService monitoringTaskQueryService;
    private final ReportGroupQueryService reportGroupQueryService;
    private final TaskDefinitionValidator taskDefinitionValidator;
    private final JsonSupport jsonSupport;

    public MonitoringTaskCommandService(
            MonitoringTaskRepository monitoringTaskRepository,
            TaskReportGroupRelationRepository taskReportGroupRelationRepository,
            MonitoringTaskQueryService monitoringTaskQueryService,
            ReportGroupQueryService reportGroupQueryService,
            TaskDefinitionValidator taskDefinitionValidator,
            JsonSupport jsonSupport
    ) {
        this.monitoringTaskRepository = monitoringTaskRepository;
        this.taskReportGroupRelationRepository = taskReportGroupRelationRepository;
        this.monitoringTaskQueryService = monitoringTaskQueryService;
        this.reportGroupQueryService = reportGroupQueryService;
        this.taskDefinitionValidator = taskDefinitionValidator;
        this.jsonSupport = jsonSupport;
    }

    public MonitoringTaskResponse create(MonitoringTaskUpsertRequest request) {
        taskDefinitionValidator.validate(request);
        List<Long> normalizedReportGroupIds = normalizeReportGroupIds(request.getReportGroupIds());
        validateReportGroupIds(normalizedReportGroupIds);

        LocalDateTime now = LocalDateTime.now();

        MonitoringTask task = new MonitoringTask();
        task.setTaskId(monitoringTaskRepository.nextTaskId());
        task.setTaskNm(request.getTaskNm());
        task.setTaskTypeCd(request.getTaskTypeCd());
        task.setTaskCntnt(request.getTaskCntnt());
        task.setExecParamJson(jsonSupport.write(request.getExecParam()));
        task.setSuccessParamJson(jsonSupport.write(request.getSuccessParam()));
        task.setScheduleVal(request.getScheduleVal());
        task.setTaskPrio(request.getTaskPrio());
        task.setActiveYn(request.getActiveYn());
        task.setFstRegDtm(now);
        task.setFnlUptDtm(now);
        monitoringTaskRepository.insert(task);

        replaceTaskReportGroups(task.getTaskId(), normalizedReportGroupIds, now);
        return monitoringTaskQueryService.getTask(task.getTaskId());
    }

    public MonitoringTaskResponse update(Long taskId, MonitoringTaskUpsertRequest request) {
        taskDefinitionValidator.validate(request);
        List<Long> normalizedReportGroupIds = normalizeReportGroupIds(request.getReportGroupIds());
        validateReportGroupIds(normalizedReportGroupIds);

        MonitoringTask existing = monitoringTaskQueryService.getTaskDomain(taskId);
        existing.setTaskNm(request.getTaskNm());
        existing.setTaskTypeCd(request.getTaskTypeCd());
        existing.setTaskCntnt(request.getTaskCntnt());
        existing.setExecParamJson(jsonSupport.write(request.getExecParam()));
        existing.setSuccessParamJson(jsonSupport.write(request.getSuccessParam()));
        existing.setScheduleVal(request.getScheduleVal());
        existing.setTaskPrio(request.getTaskPrio());
        existing.setActiveYn(request.getActiveYn());

        LocalDateTime now = LocalDateTime.now();
        existing.setFnlUptDtm(now);
        monitoringTaskRepository.update(existing);

        replaceTaskReportGroups(taskId, normalizedReportGroupIds, now);
        return monitoringTaskQueryService.getTask(taskId);
    }

    public void deactivate(Long taskId) {
        monitoringTaskQueryService.getTaskDomain(taskId);
        monitoringTaskRepository.deactivate(taskId, LocalDateTime.now());
    }

    public void activate(Long taskId) {
        monitoringTaskQueryService.getTaskDomain(taskId);
        monitoringTaskRepository.activate(taskId, LocalDateTime.now());
    }

    private void replaceTaskReportGroups(Long taskId, List<Long> reportGroupIds, LocalDateTime now) {
        taskReportGroupRelationRepository.deleteByTaskId(taskId);
        for (Long reportGroupId : reportGroupIds) {
            taskReportGroupRelationRepository.insert(taskId, reportGroupId, now, now);
        }
    }

    private void validateReportGroupIds(List<Long> reportGroupIds) {
        Set<Long> existingReportGroupIds = reportGroupQueryService.getExistingReportGroupIds(reportGroupIds);
        if (existingReportGroupIds.size() != reportGroupIds.size()) {
            throw new IllegalArgumentException("Some reportGroupIds do not exist");
        }
    }

    private List<Long> normalizeReportGroupIds(List<Long> reportGroupIds) {
        if (reportGroupIds == null || reportGroupIds.isEmpty()) {
            return List.of();
        }
        Set<Long> deduplicated = new LinkedHashSet<>();
        for (Long reportGroupId : reportGroupIds) {
            if (reportGroupId != null) {
                deduplicated.add(reportGroupId);
            }
        }
        return new ArrayList<>(deduplicated);
    }
}