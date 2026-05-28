package com.studyclock.web.controller;

import com.studyclock.common.dto.ApiResult;
import com.studyclock.timer.entity.Subject;
import com.studyclock.timer.service.SubjectService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService service;

    public SubjectController(SubjectService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResult<List<Subject>> list() {
        return ApiResult.success(service.list());
    }

    @PostMapping
    public ApiResult<Subject> create(@RequestBody Map<String, String> body) {
        return ApiResult.success(service.create(body.get("name")));
    }

    @PutMapping("/{id}")
    public ApiResult<Subject> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ApiResult.success(service.update(id, body.get("name")));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResult.success();
    }
}
