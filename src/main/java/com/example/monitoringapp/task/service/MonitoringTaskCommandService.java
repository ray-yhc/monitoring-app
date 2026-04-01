package com.example.monitoringapp.task.service;

import com.example.monitoringapp.common.json.JsonSupport;
import com.example.monitoringapp.task.domain.MonitoringTask;
import com.example.monitoringapp.task.repository.defaultdb.MonitoringTaskRepository;
import com.example.monitoringapp.task.service.dto.MonitoringTaskResponse;
import com.example.monitoringapp.task.service.dto.MonitoringTaskUpsertRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class MonitoringTaskCommandService {

    private final MonitoringTaskRepository monitoringTaskRepository;
    private final MonitoringTaskQueryService monitoringTaskQueryService;
    private final TaskDefinitionValidator taskDefinitionValidator;
    private final JsonSupport jsonSupport;

    public MonitoringTaskCommandService(
            MonitoringTaskRepository monitoringTaskRepository,
            MonitoringTaskQueryService monitoringTaskQueryService,
            TaskDefinitionValidator taskDefinitionValidator,
            JsonSupport jsonSupport
    ) {
        this.monitoringTaskRepository = monitoringTaskRepository;
        this.monitoringTaskQueryService = monitoringTaskQueryService;
        this.taskDefinitionValidator = taskDefinitionValidator;
        this.jsonSupport = jsonSupport;
    }

    public MonitoringTaskResponse create(MonitoringTaskUpsertRequest request) {
        taskDefinitionValidator.validate(request);
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
        return monitoringTaskQueryService.getTask(task.getTaskId());
    }

    public MonitoringTaskResponse update(Long taskId, MonitoringTaskUpsertRequest request) {
        taskDefinitionValidator.validate(request);
        MonitoringTask existing = monitoringTaskQueryService.getTaskDomain(taskId);
        existing.setTaskNm(request.getTaskNm());
        existing.setTaskTypeCd(request.getTaskTypeCd());
        existing.setTaskCntnt(request.getTaskCntnt());
        existing.setExecParamJson(jsonSupport.write(request.getExecParam()));
        existing.setSuccessParamJson(jsonSupport.write(request.getSuccessParam()));
        existing.setScheduleVal(request.getScheduleVal());
        existing.setTaskPrio(request.getTaskPrio());
        existing.setActiveYn(request.getActiveYn());
        existing.setFnlUptDtm(LocalDateTime.now());
        monitoringTaskRepository.update(existing);
        return monitoringTaskQueryService.getTask(taskId);
    }

    public void deactivate(Long taskId) {
        monitoringTaskQueryService.getTaskDomain(taskId);
        monitoringTaskRepository.deactivate(taskId, LocalDateTime.now());
    }
}