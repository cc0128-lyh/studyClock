import api from './index'

export interface Subject {
  id: number
  name: string
  sortOrder: number
  createdAt?: string
  updatedAt?: string
}

export const subjectApi = {
  list: () => api.get('/subjects'),
  create: (name: string) => api.post('/subjects', { name }),
  update: (id: number, name: string) => api.put(`/subjects/${id}`, { name }),
  delete: (id: number) => api.delete(`/subjects/${id}`)
}
