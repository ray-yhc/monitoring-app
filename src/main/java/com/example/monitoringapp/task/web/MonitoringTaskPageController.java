package com.example.monitoringapp.task.web;

import com.example.monitoringapp.common.json.JsonSupport;
import com.example.monitoringapp.task.domain.TaskExecutionResult;
import com.example.monitoringapp.task.domain.TaskType;
import com.example.monitoringapp.task.service.MonitoringTaskCommandService;
import com.example.monitoringapp.task.service.MonitoringTaskExecutionService;
import com.example.monitoringapp.task.service.MonitoringTaskQueryService;
import com.example.monitoringapp.task.service.ReportGroupQueryService;
import com.example.monitoringapp.task.service.dto.MonitoringTaskResponse;
import com.example.monitoringapp.task.service.dto.MonitoringTaskUpsertRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/tasks")
public class MonitoringTaskPageController {

    private final MonitoringTaskQueryService monitoringTaskQueryService;
    private final MonitoringTaskCommandService monitoringTaskCommandService;
    private final MonitoringTaskExecutionService monitoringTaskExecutionService;
    private final ReportGroupQueryService reportGroupQueryService;
    private final JsonSupport jsonSupport;

    public MonitoringTaskPageController(
            MonitoringTaskQueryService monitoringTaskQueryService,
            MonitoringTaskCommandService monitoringTaskCommandService,
            MonitoringTaskExecutionService monitoringTaskExecutionService,
            ReportGroupQueryService reportGroupQueryService,
            JsonSupport jsonSupport
    ) {
        this.monitoringTaskQueryService = monitoringTaskQueryService;
        this.monitoringTaskCommandService = monitoringTaskCommandService;
        this.monitoringTaskExecutionService = monitoringTaskExecutionService;
        this.reportGroupQueryService = reportGroupQueryService;
        this.jsonSupport = jsonSupport;
    }

    @ModelAttribute("taskTypeExamples")
    public Map<String, Map<String, String>> taskTypeExamples() {
        Map<String, Map<String, String>> examples = new LinkedHashMap<>();
        for (TaskType type : TaskType.values()) {
            Map<String, String> typeMap = new LinkedHashMap<>();
            typeMap.put("exec", type.getExecParamExample());
            typeMap.put("success", type.getSuccessParamExample());
            examples.put(type.name(), typeMap);
        }
        return examples;
    }

    @GetMapping
    public String tasks(Model model) {
        model.addAttribute("tasks", monitoringTaskQueryService.getTasks());
        return "tasks/list";
    }

    @GetMapping("/new")
    public String newTask(Model model) {
        MonitoringTaskForm form = new MonitoringTaskForm();
        form.setTaskTypeCd("URL_HEALTH_CHECK");
        form.setScheduleVal("0 * * * * *");
        form.setExecParam(TaskType.URL_HEALTH_CHECK.getExecParamExample());
        form.setSuccessParam(TaskType.URL_HEALTH_CHECK.getSuccessParamExample());
        model.addAttribute("taskForm", form);
        model.addAttribute("formMode", "create");
        model.addAttribute("formAction", "/tasks");
        model.addAttribute("reportGroups", reportGroupQueryService.getReportGroups());
        return "tasks/form";
    }

    @PostMapping
    public String createTask(@Valid @ModelAttribute("taskForm") MonitoringTaskForm taskForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "create");
            model.addAttribute("formAction", "/tasks");
            model.addAttribute("reportGroups", reportGroupQueryService.getReportGroups());
            return "tasks/form";
        }
        try {
            MonitoringTaskResponse response = monitoringTaskCommandService.create(toRequest(taskForm));
            redirectAttributes.addFlashAttribute("message", "Task created.");
            return "redirect:/tasks/" + response.getTaskId();
        } catch (IllegalArgumentException e) {
            bindingResult.reject("global", e.getMessage());
            model.addAttribute("formMode", "create");
            model.addAttribute("formAction", "/tasks");
            model.addAttribute("reportGroups", reportGroupQueryService.getReportGroups());
            return "tasks/form";
        }
    }

    @GetMapping("/{taskId}")
    public String taskDetail(@PathVariable Long taskId, Model model) {
        MonitoringTaskResponse task = monitoringTaskQueryService.getTask(taskId);
        model.addAttribute("task", task);
        model.addAttribute("execParamPretty", jsonSupport.writePretty(task.getExecParam()));
        model.addAttribute("successParamPretty", jsonSupport.writePretty(task.getSuccessParam()));
        model.addAttribute("histories", monitoringTaskQueryService.getTaskHistories(taskId, 0, 10));
        return "tasks/detail";
    }

    @GetMapping("/{taskId}/edit")
    public String editTask(@PathVariable Long taskId, Model model) {
        MonitoringTaskResponse task = monitoringTaskQueryService.getTask(taskId);
        model.addAttribute("taskForm", toForm(task));
        model.addAttribute("formMode", "edit");
        model.addAttribute("taskId", taskId);
        model.addAttribute("formAction", "/tasks/" + taskId + "/edit");
        model.addAttribute("reportGroups", reportGroupQueryService.getReportGroups());
        return "tasks/form";
    }

    @PostMapping("/{taskId}/edit")
    public String updateTask(@PathVariable Long taskId, @Valid @ModelAttribute("taskForm") MonitoringTaskForm taskForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "edit");
            model.addAttribute("taskId", taskId);
            model.addAttribute("formAction", "/tasks/" + taskId + "/edit");
            model.addAttribute("reportGroups", reportGroupQueryService.getReportGroups());
            return "tasks/form";
        }
        try {
            monitoringTaskCommandService.update(taskId, toRequest(taskForm));
            redirectAttributes.addFlashAttribute("message", "Task updated.");
            return "redirect:/tasks/" + taskId;
        } catch (IllegalArgumentException e) {
            bindingResult.reject("global", e.getMessage());
            model.addAttribute("formMode", "edit");
            model.addAttribute("taskId", taskId);
            model.addAttribute("formAction", "/tasks/" + taskId + "/edit");
            model.addAttribute("reportGroups", reportGroupQueryService.getReportGroups());
            return "tasks/form";
        }
    }

    @PostMapping("/{taskId}/delete")
    public String deleteTask(@PathVariable Long taskId, RedirectAttributes redirectAttributes) {
        monitoringTaskCommandService.deactivate(taskId);
        redirectAttributes.addFlashAttribute("message", "Task deactivated.");
        return "redirect:/tasks";
    }

    @PostMapping("/{taskId}/activate")
    public String activateTask(@PathVariable Long taskId, RedirectAttributes redirectAttributes) {
        monitoringTaskCommandService.activate(taskId);
        redirectAttributes.addFlashAttribute("message", "Task activated.");
        return "redirect:/tasks";
    }

    @GetMapping("/{taskId}/histories")
    public String taskHistories(@PathVariable Long taskId, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "20") Integer size, Model model) {
        model.addAttribute("task", monitoringTaskQueryService.getTask(taskId));
        model.addAttribute("historyPage", monitoringTaskQueryService.getTaskHistories(taskId, page, size));
        return "tasks/histories";
    }

    @PostMapping("/test")
    public String testRun(@ModelAttribute("taskForm") MonitoringTaskForm taskForm, Model model) {
        try {
            TaskExecutionResult result = monitoringTaskExecutionService.previewExecute(
                    taskForm.getTaskTypeCd(),
                    jsonSupport.readTree(taskForm.getExecParam()),
                    jsonSupport.readTree(taskForm.getSuccessParam())
            );
            model.addAttribute("testResult", result);
            model.addAttribute("testResultDataPretty",
                    result.getResultData() != null ? jsonSupport.writePretty(result.getResultData()) : null);
        } catch (Exception e) {
            model.addAttribute("testError", e.getMessage());
        }
        model.addAttribute("formMode", "create");
        model.addAttribute("formAction", "/tasks");
        model.addAttribute("reportGroups", reportGroupQueryService.getReportGroups());
        return "tasks/form";
    }

    @PostMapping("/{taskId}/restart")
    public String restartTask(@PathVariable Long taskId, RedirectAttributes redirectAttributes) {
        monitoringTaskExecutionService.executeNow(taskId);
        redirectAttributes.addFlashAttribute("message", "Task executed.");
        return "redirect:/tasks/" + taskId;
    }

    private MonitoringTaskUpsertRequest toRequest(MonitoringTaskForm form) {
        MonitoringTaskUpsertRequest request = new MonitoringTaskUpsertRequest();
        request.setTaskNm(form.getTaskNm());
        request.setTaskTypeCd(form.getTaskTypeCd());
        request.setTaskCntnt(form.getTaskCntnt());
        request.setExecParam(jsonSupport.readTree(form.getExecParam()));
        request.setSuccessParam(jsonSupport.readTree(form.getSuccessParam()));
        request.setScheduleVal(form.getScheduleVal());
        request.setTaskPrio(form.getTaskPrio());
        request.setActiveYn(form.getActiveYn());
        request.setReportGroupIds(form.getReportGroupIds());
        return request;
    }

    private MonitoringTaskForm toForm(MonitoringTaskResponse response) {
        MonitoringTaskForm form = new MonitoringTaskForm();
        form.setTaskId(response.getTaskId());
        form.setTaskNm(response.getTaskNm());
        form.setTaskTypeCd(response.getTaskTypeCd());
        form.setTaskCntnt(response.getTaskCntnt());
        form.setExecParam(jsonSupport.writePretty(response.getExecParam()));
        form.setSuccessParam(jsonSupport.writePretty(response.getSuccessParam()));
        form.setScheduleVal(response.getScheduleVal());
        form.setTaskPrio(response.getTaskPrio());
        form.setActiveYn(response.getActiveYn());
        form.setReportGroupIds(response.getReportGroupIds());
        return form;
    }
}