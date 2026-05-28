import api from './index'

export interface FocusSession {
  id: number
  startTime: string
  endTime: string | null
  targetMinutes: number
  targetSeconds: number
  actualSeconds: number
  status: 'RUNNING' | 'PAUSED' | 'COMPLETED' | 'CANCELLED'
  note: string | null
  subjectName: string | null
  examMode: boolean | null
  examPaperName: string | null
  wrongQuestions: string | null
  examTotalScore: number | null
  examScore: number | null
}

export const timerApi = {
  start: (minutes = 25, seconds = 0, subjectName?: string) =>
    api.post('/timer/start', null, {
      params: { minutes, seconds, subjectName }
    }),
  pause: (id: number) => api.post(`/timer/${id}/pause`),
  resume: (id: number) => api.post(`/timer/${id}/resume`),
  complete: (id: number, actualSeconds: number, examData?: {
    examMode?: boolean
    examPaperName?: string
    wrongQuestions?: string
    examTotalScore?: number
    examScore?: number
  }) => {
    const params: Record<string, any> = { actualSeconds }
    if (examData) {
      for (const [k, v] of Object.entries(examData)) {
        if (v !== undefined && v !== null) params[k] = v
      }
    }
    return api.post(`/timer/${id}/complete`, null, { params })
  },
  cancel: (id: number) => api.post(`/timer/${id}/cancel`),
  current: () => api.get('/timer/current'),
  history: (date?: string) => api.get('/timer/history', { params: { date } }),
  examHistory: (date?: string) => api.get('/timer/exam-history', { params: { date } }),
  addDebug: (startTime: string, endTime: string, subjectName?: string) =>
    api.post('/timer/debug/add', { startTime, endTime, subjectName })
}
