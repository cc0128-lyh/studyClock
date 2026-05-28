package com.studyclock.shortcut.service;

import com.studyclock.shortcut.entity.Shortcut;
import com.studyclock.shortcut.repository.ShortcutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ShortcutService {

    private final ShortcutRepository repository;

    public ShortcutService(ShortcutRepository repository) {
        this.repository = repository;
    }

    public List<Shortcut> listAll() {
        return repository.findAllByOrderBySortOrderAsc();
    }

    public Shortcut getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shortcut not found: " + id));
    }

    public Shortcut save(Shortcut shortcut) {
        return repository.save(shortcut);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public String launch(Long id) {
        Shortcut shortcut = getById(id);
        return switch (shortcut.getLaunchType()) {
            case "APP" -> shortcut.getAppPath();
            case "URL" -> shortcut.getUrl();
            case "COMMAND" -> shortcut.getAppPath();
            default -> throw new RuntimeException("Unknown launch type: " + shortcut.getLaunchType());
        };
    }
}
