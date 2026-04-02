package com.example.monitoringapp.task.service;

import com.example.monitoringapp.task.domain.ReportGroup;
import com.example.monitoringapp.task.repository.defaultdb.ReportGroupRepository;
import com.example.monitoringapp.task.service.dto.ReportGroupResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReportGroupQueryService {

    private final ReportGroupRepository reportGroupRepository;

    public ReportGroupQueryService(ReportGroupRepository reportGroupRepository) {
        this.reportGroupRepository = reportGroupRepository;
    }

    public List<ReportGroupResponse> getReportGroups() {
        return reportGroupRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ReportGroupResponse getReportGroup(Long reportGroupId) {
        ReportGroup reportGroup = reportGroupRepository.findById(reportGroupId);
        if (reportGroup == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ReportGroup not found: " + reportGroupId);
        }
        return toResponse(reportGroup);
    }

    public Set<Long> getExistingReportGroupIds(List<Long> reportGroupIds) {
        if (reportGroupIds == null || reportGroupIds.isEmpty()) {
            return Set.of();
        }
        return new LinkedHashSet<>(
                reportGroupRepository.findByIds(reportGroupIds)
                        .stream()
                        .map(ReportGroup::getReportGroupId)
                        .toList()
        );
    }

    private ReportGroupResponse toResponse(ReportGroup reportGroup) {
        ReportGroupResponse response = new ReportGroupResponse();
        response.setReportGroupId(reportGroup.getReportGroupId());
        response.setReportGroupName(reportGroup.getReportGroupName());
        response.setDescription(reportGroup.getDescription());
        response.setChatRoomId(reportGroup.getChatRoomId());
        response.setSendYn(reportGroup.getSendYn());
        return response;
    }
}