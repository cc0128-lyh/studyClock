package com.studyclock.timer.entity;

import com.studyclock.common.entity.BaseEntity;
import com.studyclock.common.enums.FocusStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "focus_sessions")
public class FocusSession extends BaseEntity {

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "target_minutes")
    private Integer targetMinutes;

    @Column(name = "target_seconds", columnDefinition = "INTEGER DEFAULT 0")
    private Integer targetSeconds = 0;

    @Column(name = "actual_seconds")
    private Long actualSeconds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FocusStatus status;

    @Column(length = 500)
    private String note;

    @Column(name = "subject_name", length = 100)
    private String subjectName;

    @Column(name = "exam_mode")
    private Boolean examMode = false;

    @Column(name = "exam_paper_name", length = 200)
    private String examPaperName;

    @Column(name = "wrong_questions", columnDefinition = "TEXT")
    private String wrongQuestions;

    @Column(name = "exam_total_score")
    private Integer examTotalScore;

    @Column(name = "exam_score")
    private Integer examScore;

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Integer getTargetMinutes() { return targetMinutes; }
    public void setTargetMinutes(Integer targetMinutes) { this.targetMinutes = targetMinutes; }
    public Integer getTargetSeconds() { return targetSeconds; }
    public void setTargetSeconds(Integer targetSeconds) { this.targetSeconds = targetSeconds; }
    public Long getActualSeconds() { return actualSeconds; }
    public void setActualSeconds(Long actualSeconds) { this.actualSeconds = actualSeconds; }
    public FocusStatus getStatus() { return status; }
    public void setStatus(FocusStatus status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public Boolean getExamMode() { return examMode; }
    public void setExamMode(Boolean examMode) { this.examMode = examMode; }
    public String getExamPaperName() { return examPaperName; }
    public void setExamPaperName(String examPaperName) { this.examPaperName = examPaperName; }
    public String getWrongQuestions() { return wrongQuestions; }
    public void setWrongQuestions(String wrongQuestions) { this.wrongQuestions = wrongQuestions; }
    public Integer getExamTotalScore() { return examTotalScore; }
    public void setExamTotalScore(Integer examTotalScore) { this.examTotalScore = examTotalScore; }
    public Integer getExamScore() { return examScore; }
    public void setExamScore(Integer examScore) { this.examScore = examScore; }
}
