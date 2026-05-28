package com.studyclock.web.controller;

import com.studyclock.common.dto.ApiResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Value("${studyclock.home:./}")
    private String studyclockHome;

    @GetMapping
    public ApiResult<Map<String, Object>> health() {
        return ApiResult.success(Map.of(
            "status", "UP",
            "studyclockHome", studyclockHome
        ));
    }
}
