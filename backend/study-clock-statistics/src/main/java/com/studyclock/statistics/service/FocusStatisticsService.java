package com.studyclock.statistics.service;

import com.studyclock.statistics.dto.FocusSummaryDTO;
import com.studyclock.statistics.dto.FocusSummaryDTO.SubjectBreakdownItem;
import com.studyclock.timer.repository.FocusSessionRepository;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class FocusStatisticsService {

    private final FocusSessionRepository repository;

    public FocusStatisticsService(FocusSessionRepository repository) {
        this.repository = repository;
    }

    public FocusSummaryDTO getSummary() {
        FocusSummaryDTO dto = new FocusSummaryDTO();
        LocalDate today = LocalDate.now();

        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.atTime(LocalTime.MAX);

        LocalDateTime weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(LocalTime.MAX);

        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = today.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        dto.setTotalSeconds(repository.sumActualSecondsByDateRange(LocalDate.of(2000, 1, 1).atStartOfDay(), LocalDateTime.now()));
        dto.setDailySeconds(repository.sumActualSecondsByDateRange(dayStart, dayEnd));
        dto.setWeeklySeconds(repository.sumActualSecondsByDateRange(weekStart, weekEnd));
        dto.setMonthlySeconds(repository.sumActualSecondsByDateRange(monthStart, monthEnd));
        dto.setSessionCount(repository.findAll().size());

        // subject breakdown — 今日学科分布
        List<Object[]> rows = repository.sumBySubjectByDateRange(dayStart, dayEnd);
        dto.setSubjectBreakdown(buildBreakdown(rows));

        return dto;
    }

    public FocusSummaryDTO getSubjectBreakdown(String range) {
        FocusSummaryDTO dto = new FocusSummaryDTO();
        LocalDate today = LocalDate.now();
        LocalDateTime start;
        LocalDateTime end = today.atTime(LocalTime.MAX);

        switch (range.toUpperCase()) {
            case "TODAY":
                start = today.atStartOfDay();
                break;
            case "MONTH":
                start = today.withDayOfMonth(1).atStartOfDay();
                break;
            case "TOTAL":
                start = LocalDate.of(2000, 1, 1).atStartOfDay();
                break;
            default: // WEEK
                start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
                break;
        }

        List<Object[]> rows = repository.sumBySubjectByDateRange(start, end);
        dto.setSubjectBreakdown(buildBreakdown(rows));
        return dto;
    }

    private List<SubjectBreakdownItem> buildBreakdown(List<Object[]> rows) {
        List<SubjectBreakdownItem> breakdown = new ArrayList<>();
        for (Object[] row : rows) {
            String subjectName = (String) row[0];
            long count = ((Number) row[1]).longValue();
            long secs = ((Number) row[2]).longValue();
            breakdown.add(new SubjectBreakdownItem(
                subjectName != null ? subjectName : "其他",
                (int) count,
                secs
            ));
        }
        return breakdown;
    }
}
