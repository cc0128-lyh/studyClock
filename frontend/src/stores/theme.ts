import { defineStore } from 'pinia'
import { ref } from 'vue'
import { settingsApi } from '@/api/settings'

export const useThemeStore = defineStore('theme', () => {
  const themeMode = ref<'dark' | 'light'>('dark')

  async function initTheme() {
    try {
      const res: any = await settingsApi.get('theme')
      if (res.data === 'light' || res.data === 'dark') {
        themeMode.value = res.data
      }
    } catch (_) {}
    applyTheme(themeMode.value)
  }

  function applyTheme(mode: 'dark' | 'light') {
    document.documentElement.dataset.theme = mode
  }

  async function setTheme(mode: 'dark' | 'light') {
    themeMode.value = mode
    applyTheme(mode)
    try {
      await settingsApi.set('theme', mode)
    } catch (e) {
      console.error('Failed to save theme setting:', e)
    }
  }

  return { themeMode, initTheme, setTheme }
})
