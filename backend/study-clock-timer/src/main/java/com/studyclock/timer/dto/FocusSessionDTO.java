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
}
