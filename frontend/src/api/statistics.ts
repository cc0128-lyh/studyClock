import api from './index'

export interface SubjectBreakdownItem {
  subjectName: string
  sessionCount: number
  totalSeconds: number
}

export interface FocusSummary {
  totalSeconds: number
  sessionCount: number
  dailySessionCount: number
  weeklySessionCount: number
  monthlySessionCount: number
  dailySeconds: number
  weeklySeconds: number
  monthlySeconds: number
  subjectBreakdown: SubjectBreakdownItem[]
}

export const statisticsApi = {
  summary: () => api.get('/statistics/summary'),
  subjectBreakdown: (range: string = 'WEEK') =>
    api.get('/statistics/subject-breakdown', { params: { range } })
}
