package com.studyclock.web.controller;

import com.studyclock.common.dto.ApiResult;
import com.studyclock.shortcut.entity.Shortcut;
import com.studyclock.shortcut.service.ShortcutService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shortcuts")
public class ShortcutController {

    private final ShortcutService service;

    public ShortcutController(ShortcutService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResult<List<Shortcut>> list() {
        return ApiResult.success(service.listAll());
    }

    @PostMapping
    public ApiResult<Shortcut> create(@RequestBody Shortcut shortcut) {
        return ApiResult.success(service.save(shortcut));
    }

    @PutMapping("/{id}")
    public ApiResult<Shortcut> update(@PathVariable Long id, @RequestBody Shortcut shortcut) {
        Shortcut existing = service.getById(id);
        existing.setName(shortcut.getName());
        existing.setAppPath(shortcut.getAppPath());
        existing.setUrl(shortcut.getUrl());
        existing.setIcon(shortcut.getIcon());
        existing.setSortOrder(shortcut.getSortOrder());
        existing.setLaunchType(shortcut.getLaunchType());
        return ApiResult.success(service.save(existing));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResult.success();
    }

    @PostMapping("/{id}/launch")
    public ApiResult<String> launch(@PathVariable Long id) {
        return ApiResult.success(service.launch(id));
    }
}
