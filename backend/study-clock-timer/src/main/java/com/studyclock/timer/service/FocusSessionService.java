package com.studyclock.timer.service;

import com.studyclock.common.enums.FocusStatus;
import com.studyclock.timer.dto.FocusSessionDTO;
import com.studyclock.timer.entity.FocusSession;
import com.studyclock.timer.repository.FocusSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class FocusSessionService {

    private final FocusSessionRepository repository;

    public FocusSessionService(FocusSessionRepository repository) {
        this.repository = repository;
    }

    public FocusSessionDTO startSession(Integer targetMinutes, Integer targetSeconds, String subjectName) {
        FocusSession session = new FocusSession();
        session.setStartTime(LocalDateTime.now());
        session.setTargetMinutes(targetMinutes);
        session.setTargetSeconds(targetSeconds != null ? targetSeconds : 0);
        session.setSubjectName(subjectName);
        session.setStatus(FocusStatus.RUNNING);
        session.setActualSeconds(0L);
        return toDTO(repository.save(session));
    }

    public FocusSessionDTO pauseSession(Long id) {
        FocusSession session = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found: " + id));
        session.setStatus(FocusStatus.PAUSED);
        return toDTO(repository.save(session));
    }

    public FocusSessionDTO resumeSession(Long id) {
        FocusSession session = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found: " + id));
        session.setStatus(FocusStatus.RUNNING);
        return toDTO(repository.save(session));
    }

    public FocusSessionDTO completeSession(Long id, Long actualSeconds,
            Boolean examMode, String examPaperName, String wrongQuestions,
            Integer examTotalScore, Integer examScore) {
        FocusSession session = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found: " + id));
        session.setEndTime(LocalDateTime.now());
        session.setActualSeconds(actualSeconds);
        session.setStatus(FocusStatus.COMPLETED);
        if (examMode != null) session.setExamMode(examMode);
        if (examPaperName != null) session.setExamPaperName(examPaperName);
        if (wrongQuestions != null) session.setWrongQuestions(wrongQuestions);
        if (examTotalScore != null) session.setExamTotalScore(examTotalScore);
        if (examScore != null) session.setExamScore(examScore);
        return toDTO(repository.save(session));
    }

    public FocusSessionDTO cancelSession(Long id) {
        FocusSession session = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found: " + id));
        session.setEndTime(LocalDateTime.now());
        session.setStatus(FocusStatus.CANCELLED);
        return toDTO(repository.save(session));
    }

    public FocusSessionDTO addDebugSession(LocalDateTime startTime, LocalDateTime endTime, String subjectName) {
        if (startTime == null || endTime == null) {
            throw new RuntimeException("startTime and endTime are required");
        }
        if (endTime.isAfter(LocalDateTime.now())) {
            throw new RuntimeException("endTime cannot be in the future");
        }
        if (!endTime.isAfter(startTime)) {
            throw new RuntimeException("endTime must be after startTime");
        }
        long actualSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
        if (actualSeconds <= 0) {
            throw new RuntimeException("duration must be positive");
        }
        long totalMinutes = actualSeconds / 60;
        int targetMinutes = (int) totalMinutes;
        int targetSeconds = (int) (actualSeconds % 60);

        FocusSession session = new FocusSession();
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setTargetMinutes(targetMinutes);
        session.setTargetSeconds(targetSeconds);
        session.setActualSeconds(actualSeconds);
        session.setSubjectName(subjectName);
        session.setStatus(FocusStatus.COMPLETED);
        return toDTO(repository.save(session));
    }

    public FocusSessionDTO getCurrentSession() {
        List<FocusSession> sessions = repository.findByStatusOrderByStartTimeDesc(FocusStatus.RUNNING);
        return sessions.isEmpty() ? null : toDTO(sessions.get(0));
    }

    public List<FocusSessionDTO> getHistory(LocalDateTime start, LocalDateTime end) {
        return repository.findByDateRange(start, end).stream()
                .map(this::toDTO).toList();
    }

    public List<FocusSessionDTO> listAll() {
        return repository.findAll().stream()
                .map(this::toDTO).toList();
    }

    public List<FocusSessionDTO> getExamHistory(LocalDateTime start, LocalDateTime end) {
        return repository.findExamSessionsByDateRange(start, end).stream()
                .map(this::toDTO).toList();
    }

    FocusSession findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found: " + id));
    }

    private FocusSessionDTO toDTO(FocusSession entity) {
        FocusSessionDTO dto = new FocusSessionDTO();
        dto.setId(entity.getId());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setTargetMinutes(entity.getTargetMinutes());
        dto.setTargetSeconds(entity.getTargetSeconds());
        dto.setActualSeconds(entity.getActualSeconds());
        dto.setStatus(entity.getStatus());
        dto.setNote(entity.getNote());
        dto.setSubjectName(entity.getSubjectName());
        dto.setExamMode(entity.getExamMode());
        dto.setExamPaperName(entity.getExamPaperName());
        dto.setWrongQuestions(entity.getWrongQuestions());
        dto.setExamTotalScore(entity.getExamTotalScore());
        dto.setExamScore(entity.getExamScore());
        return dto;
    }
}
