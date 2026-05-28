import { defineStore } from 'pinia'
import { ref } from 'vue'
import { settingsApi } from '@/api/settings'

export const useUserStore = defineStore('user', () => {
  const nickname = ref('')
  const avatarUrl = ref('')

  async function fetchProfile() {
    try {
      const [nickRes, avaRes] = await Promise.all([
        settingsApi.get('userNickname'),
        settingsApi.get('userAvatar')
      ])
      if ((nickRes as any).data) nickname.value = (nickRes as any).data
      if ((avaRes as any).data) avatarUrl.value = (avaRes as any).data
    } catch (_) {}
  }

  async function saveNickname(name: string) {
    nickname.value = name
    await settingsApi.set('userNickname', name)
  }

  async function saveAvatar(url: string) {
    avatarUrl.value = url
    await settingsApi.set('userAvatar', url)
  }

  return { nickname, avatarUrl, fetchProfile, saveNickname, saveAvatar }
})
