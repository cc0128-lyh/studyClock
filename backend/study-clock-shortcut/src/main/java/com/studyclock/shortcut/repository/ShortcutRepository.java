package com.studyclock.shortcut.repository;

import com.studyclock.shortcut.entity.Shortcut;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShortcutRepository extends JpaRepository<Shortcut, Long> {

    List<Shortcut> findAllByOrderBySortOrderAsc();

    List<Shortcut> findByLaunchTypeOrderBySortOrderAsc(String launchType);
}
