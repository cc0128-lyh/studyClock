package com.studyclock.settings.service;

import com.studyclock.settings.entity.AppSetting;
import com.studyclock.settings.repository.AppSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AppSettingService {

    private final AppSettingRepository repository;

    public AppSettingService(AppSettingRepository repository) {
        this.repository = repository;
    }

    public String get(String key) {
        return repository.findById(key).map(AppSetting::getSettingValue).orElse(null);
    }

    public String get(String key, String defaultValue) {
        return repository.findById(key).map(AppSetting::getSettingValue).orElse(defaultValue);
    }

    public void set(String settingKey, String value) {
        AppSetting setting = repository.findById(settingKey).orElse(new AppSetting());
        setting.setSettingKey(settingKey);
        setting.setSettingValue(value);
        repository.save(setting);
    }

    public Map<String, String> getAll() {
        List<AppSetting> settings = repository.findAll();
        Map<String, String> map = new HashMap<>();
        settings.forEach(s -> map.put(s.getSettingKey(), s.getSettingValue()));
        return map;
    }

    public void delete(String settingKey) {
        repository.deleteById(settingKey);
    }
}
