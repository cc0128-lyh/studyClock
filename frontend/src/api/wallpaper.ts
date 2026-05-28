import api from './index'

export interface Wallpaper {
  id: number
  name: string
  filePath: string | null
  bgColor: string | null
  type: 'IMAGE' | 'COLOR'
  isActive: boolean
  sortOrder: number
}

export const wallpaperApi = {
  list: () => api.get('/wallpapers'),
  active: () => api.get('/wallpapers/active'),
  create: (data: Partial<Wallpaper>) => api.post('/wallpapers', data),
  upload: (file: File) => {
    const fd = new FormData()
    fd.append('file', file)
    return api.post('/wallpapers/upload', fd, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  setActive: (id: number) => api.put(`/wallpapers/${id}/active`),
  delete: (id: number) => api.delete(`/wallpapers/${id}`)
}
