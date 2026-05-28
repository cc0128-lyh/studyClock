import { defineStore } from 'pinia'
import { ref } from 'vue'
import { wallpaperApi, type Wallpaper } from '@/api/wallpaper'

export const useWallpaperStore = defineStore('wallpaper', () => {
  const activeWallpaper = ref<Wallpaper | null>(null)
  const list = ref<Wallpaper[]>([])

  async function fetchActive() {
    try {
      const res: any = await wallpaperApi.active()
      activeWallpaper.value = res.data
    } catch (e) {
      console.error('Failed to fetch active wallpaper:', e)
    }
  }

  async function fetchList() {
    try {
      const res: any = await wallpaperApi.list()
      list.value = res.data
    } catch (e) {
      console.error('Failed to fetch wallpapers:', e)
    }
  }

  async function setActive(id: number) {
    try {
      const res: any = await wallpaperApi.setActive(id)
      activeWallpaper.value = res.data
    } catch (e) {
      console.error('Failed to set active wallpaper:', e)
    }
  }

  async function upload(file: File) {
    const res: any = await wallpaperApi.upload(file)
    await fetchList()
    return res
  }

  const bgStyle = ref('')

  function updateBgStyle() {
    const wp = activeWallpaper.value
    if (!wp) {
      bgStyle.value = 'background: #1a1a2e;'
      return
    }
    if (wp.type === 'COLOR') {
      bgStyle.value = `background: ${wp.bgColor || '#1a1a2e'};`
    } else {
      bgStyle.value = `background: url(${wp.filePath}) center/cover no-repeat;`
    }
  }

  return { activeWallpaper, list, bgStyle, fetchActive, fetchList, setActive, upload, updateBgStyle }
})
