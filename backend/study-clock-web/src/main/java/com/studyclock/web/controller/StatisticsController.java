package com.studyclock.web.controller;

import com.studyclock.common.dto.ApiResult;
import com.studyclock.statistics.dto.FocusSummaryDTO;
import com.studyclock.statistics.service.FocusStatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final FocusStatisticsService service;

    public StatisticsController(FocusStatisticsService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public ApiResult<FocusSummaryDTO> summary() {
        return ApiResult.success(service.getSummary());
    }

    @GetMapping("/subject-breakdown")
    public ApiResult<FocusSummaryDTO> subjectBreakdown(@RequestParam(defaultValue = "WEEK") String range) {
        return ApiResult.success(service.getSubjectBreakdown(range));
    }
}
