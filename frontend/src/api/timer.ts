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
}

export const timerApi = {
  start: (minutes = 25, seconds = 0, subjectName?: string) =>
    api.post('/timer/start', null, {
      params: { minutes, seconds, subjectName }
    }),
  pause: (id: number) => api.post(`/timer/${id}/pause`),
  resume: (id: number) => api.post(`/timer/${id}/resume`),
  complete: (id: number, actualSeconds: number) =>
    api.post(`/timer/${id}/complete`, null, { params: { actualSeconds } }),
  cancel: (id: number) => api.post(`/timer/${id}/cancel`),
  current: () => api.get('/timer/current'),
  history: (date?: string) => api.get('/timer/history', { params: { date } }),
  addDebug: (startTime: string, endTime: string, subjectName?: string) =>
    api.post('/timer/debug/add', { startTime, endTime, subjectName })
}
