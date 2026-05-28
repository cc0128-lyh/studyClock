import api from './index'

export interface Shortcut {
  id: number
  name: string
  appPath: string | null
  url: string | null
  icon: string | null
  sortOrder: number
  launchType: 'APP' | 'URL' | 'COMMAND'
}

export const shortcutApi = {
  list: () => api.get('/shortcuts'),
  create: (data: Partial<Shortcut>) => api.post('/shortcuts', data),
  update: (id: number, data: Partial<Shortcut>) => api.put(`/shortcuts/${id}`, data),
  delete: (id: number) => api.delete(`/shortcuts/${id}`),
  launch: (id: number) => api.post(`/shortcuts/${id}/launch`)
}
