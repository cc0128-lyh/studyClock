package com.studyclock.timer.dto;

import com.studyclock.common.enums.FocusStatus;
import java.time.LocalDateTime;

public class FocusSessionDTO {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer targetMinutes;
    private Integer targetSeconds;
    private Long actualSeconds;
    private FocusStatus status;
    private String note;
    private String subjectName;
    private Boolean examMode;
    private String examPaperName;
    private String wrongQuestions;
    private Integer examTotalScore;
    private Integer examScore;

    public FocusSessionDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
