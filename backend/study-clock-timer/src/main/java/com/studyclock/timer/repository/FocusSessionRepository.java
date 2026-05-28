package com.studyclock.timer.repository;

import com.studyclock.common.enums.FocusStatus;
import com.studyclock.timer.entity.FocusSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {

    List<FocusSession> findByStatusOrderByStartTimeDesc(FocusStatus status);

    @Query("SELECT f FROM FocusSession f WHERE f.startTime BETWEEN :start AND :end ORDER BY f.startTime DESC")
    List<FocusSession> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(f.actualSeconds), 0) FROM FocusSession f WHERE f.status = 'COMPLETED' AND f.startTime BETWEEN :start AND :end")
    Long sumActualSecondsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(f) FROM FocusSession f WHERE f.status = 'COMPLETED' AND f.startTime BETWEEN :start AND :end")
    Long countByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT f.subjectName, COUNT(f), COALESCE(SUM(f.actualSeconds), 0) FROM FocusSession f WHERE f.status = 'COMPLETED' GROUP BY f.subjectName")
    List<Object[]> sumBySubject();

    @Query("SELECT f.subjectName, COUNT(f), COALESCE(SUM(f.actualSeconds), 0) FROM FocusSession f WHERE f.status = 'COMPLETED' AND f.startTime BETWEEN :start AND :end GROUP BY f.subjectName")
    List<Object[]> sumBySubjectByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT f FROM FocusSession f WHERE f.examMode = true AND f.status = 'COMPLETED' AND f.startTime BETWEEN :start AND :end ORDER BY f.startTime DESC")
    List<FocusSession> findExamSessionsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
