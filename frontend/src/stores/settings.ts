import { defineStore } from 'pinia'
import { ref } from 'vue'
import { settingsApi } from '@/api/settings'

export const useSettingsStore = defineStore('settings', () => {
  const settings = ref<Record<string, string>>({})

  async function fetchAll() {
    try {
      const res: any = await settingsApi.getAll()
      settings.value = res.data || {}
    } catch (e) {
      console.error('Failed to fetch settings:', e)
    }
  }

  async function set(key: string, value: string) {
    try {
      await settingsApi.set(key, value)
      settings.value[key] = value
    } catch (e) {
      console.error('Failed to save setting:', e)
    }
  }

  function get(key: string, defaultValue = ''): string {
    return settings.value[key] ?? defaultValue
  }

  return { settings, fetchAll, set, get }
})
