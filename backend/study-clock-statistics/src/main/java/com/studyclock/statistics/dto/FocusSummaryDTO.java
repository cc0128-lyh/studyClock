package com.studyclock.statistics.dto;

import java.util.List;

public class FocusSummaryDTO {

    private long totalSeconds;
    private int sessionCount;
    private long dailySeconds;
    private long weeklySeconds;
    private long monthlySeconds;
    private List<SubjectBreakdownItem> subjectBreakdown;

    public long getTotalSeconds() { return totalSeconds; }
    public void setTotalSeconds(long totalSeconds) { this.totalSeconds = totalSeconds; }
    public int getSessionCount() { return sessionCount; }
    public void setSessionCount(int sessionCount) { this.sessionCount = sessionCount; }
    public long getDailySeconds() { return dailySeconds; }
    public void setDailySeconds(long dailySeconds) { this.dailySeconds = dailySeconds; }
    public long getWeeklySeconds() { return weeklySeconds; }
    public void setWeeklySeconds(long weeklySeconds) { this.weeklySeconds = weeklySeconds; }
    public long getMonthlySeconds() { return monthlySeconds; }
    public void setMonthlySeconds(long monthlySeconds) { this.monthlySeconds = monthlySeconds; }
    public List<SubjectBreakdownItem> getSubjectBreakdown() { return subjectBreakdown; }
    public void setSubjectBreakdown(List<SubjectBreakdownItem> subjectBreakdown) { this.subjectBreakdown = subjectBreakdown; }

    public static class SubjectBreakdownItem {
        private String subjectName;
        private int sessionCount;
        private long totalSeconds;

        public SubjectBreakdownItem() {}

        public SubjectBreakdownItem(String subjectName, int sessionCount, long totalSeconds) {
            this.subjectName = subjectName;
            this.sessionCount = sessionCount;
            this.totalSeconds = totalSeconds;
        }

        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
        public int getSessionCount() { return sessionCount; }
        public void setSessionCount(int sessionCount) { this.sessionCount = sessionCount; }
        public long getTotalSeconds() { return totalSeconds; }
        public void setTotalSeconds(long totalSeconds) { this.totalSeconds = totalSeconds; }
    }
}
