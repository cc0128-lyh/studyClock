import api from './index'

export const settingsApi = {
  getAll: () => api.get('/settings'),
  get: (key: string) => api.get(`/settings/${key}`),
  set: (key: string, value: string) => api.put(`/settings/${key}`, value, {
    headers: { 'Content-Type': 'text/plain' }
  }),
  delete: (key: string) => api.delete(`/settings/${key}`),
  uploadAvatar: (file: File) => {
    const form = new FormData()
    form.append('file', file)
    return api.post('/settings/upload/avatar', form)
  }
}
