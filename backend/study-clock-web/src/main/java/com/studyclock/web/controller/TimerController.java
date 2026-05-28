package com.studyclock.web.controller;

import com.studyclock.common.dto.ApiResult;
import com.studyclock.timer.dto.FocusSessionDTO;
import com.studyclock.timer.service.FocusSessionService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/timer")
public class TimerController {

    private final FocusSessionService service;

    public TimerController(FocusSessionService service) {
        this.service = service;
    }

    @PostMapping("/start")
    public ApiResult<FocusSessionDTO> start(
            @RequestParam(defaultValue = "25") Integer minutes,
            @RequestParam(required = false) Integer seconds,
            @RequestParam(required = false) String subjectName) {
        return ApiResult.success(service.startSession(minutes, seconds, subjectName));
    }

    @PostMapping("/{id}/pause")
    public ApiResult<FocusSessionDTO> pause(@PathVariable Long id) {
        return ApiResult.success(service.pauseSession(id));
    }

    @PostMapping("/{id}/resume")
    public ApiResult<FocusSessionDTO> resume(@PathVariable Long id) {
        return ApiResult.success(service.resumeSession(id));
    }

    @PostMapping("/{id}/complete")
    public ApiResult<FocusSessionDTO> complete(@PathVariable Long id, @RequestParam Long actualSeconds) {
        return ApiResult.success(service.completeSession(id, actualSeconds));
    }

    @PostMapping("/{id}/cancel")
    public ApiResult<FocusSessionDTO> cancel(@PathVariable Long id) {
        return ApiResult.success(service.cancelSession(id));
    }

    @GetMapping("/current")
    public ApiResult<FocusSessionDTO> current() {
        return ApiResult.success(service.getCurrentSession());
    }

    @GetMapping("/history")
    public ApiResult<List<FocusSessionDTO>> history(
            @RequestParam(required = false) String date) {
        if (date != null) {
            LocalDate d = LocalDate.parse(date);
            return ApiResult.success(service.getHistory(d.atStartOfDay(), d.atTime(LocalTime.MAX)));
        }
        return ApiResult.success(service.listAll());
    }

    @PostMapping("/debug/add")
    public ApiResult<FocusSessionDTO> addDebug(@RequestBody Map<String, String> body) {
        LocalDateTime startTime = LocalDateTime.parse(body.get("startTime"));
        LocalDateTime endTime = LocalDateTime.parse(body.get("endTime"));
        String subjectName = body.get("subjectName");
        return ApiResult.success(service.addDebugSession(startTime, endTime, subjectName));
    }
}
