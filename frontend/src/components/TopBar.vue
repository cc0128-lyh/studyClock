<template>
  <div class="top-bar">
    <div class="top-left">
      <div class="user-info" @click="$router.push('/settings')">
        <div class="mini-avatar" :style="avatarBgStyle">
          <span v-if="!userStore.avatarUrl">{{ displayName.charAt(0) }}</span>
          <img v-else :src="resolveBackendUrl(userStore.avatarUrl)" alt="" />
        </div>
        <span class="user-name">{{ displayName }}</span>
      </div>
    </div>
    <div class="top-center">
      <router-link to="/" class="nav-link">专注</router-link>
      <router-link to="/statistics" class="nav-link">统计</router-link>
      <router-link to="/settings" class="nav-link">设置</router-link>
    </div>
    <div class="top-right">
      <span class="clock-text">{{ currentTime }}</span>
      <button v-if="hasElectronAPI" class="top-btn" @click="toggleFullscreen">⛶</button>
      <button v-if="hasElectronAPI" class="top-btn" @click="quit">✕</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { resolveBackendUrl } from '@/api'

const userStore = useUserStore()
const currentTime = ref('')
const hasElectronAPI = ref(!!(window as any).electronAPI)
let timeInterval: ReturnType<typeof setInterval>

const displayName = computed(() => userStore.nickname || 'Study')
const avatarBgStyle = computed(() => {
  if (userStore.avatarUrl) {
    return { backgroundImage: `url(${resolveBackendUrl(userStore.avatarUrl)})`, backgroundSize: 'cover' }
  }
  return { background: '#fff' }
})

function updateTime() {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  updateTime()
  timeInterval = setInterval(updateTime, 30000)
  userStore.fetchProfile()
})
onUnmounted(() => clearInterval(timeInterval))

function toggleFullscreen() {
  window.electronAPI?.toggleFullscreen()
}
function quit() {
  window.electronAPI?.quitApp()
}
</script>

<style scoped>
.top-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1.5rem;
  background: var(--bg-glass);
  backdrop-filter: blur(24px) saturate(1.4);
  -webkit-backdrop-filter: blur(24px) saturate(1.4);
  border-bottom: 1px solid var(--glass-border);
  z-index: 100;
  -webkit-app-region: drag;
}
.top-left, .top-center, .top-right {
  display: flex;
  align-items: center;
  gap: 1rem;
  -webkit-app-region: no-drag;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  padding: 0.2rem 0.5rem;
  border-radius: 8px;
  transition: background 0.2s;
}
.user-info:hover { background: var(--bg-hover); }
.mini-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 600;
  overflow: hidden;
  flex-shrink: 0;
  color: var(--text-inverse);
}
.mini-avatar img { width: 100%; height: 100%; object-fit: cover; }
.user-name { font-size: 0.85rem; opacity: 0.85; max-width: 100px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.app-title { font-size: 0.9rem; font-weight: 600; letter-spacing: 0.1em; opacity: 0.9; }
.nav-link {
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 0.85rem;
  padding: 0.3rem 0.8rem;
  border-radius: 6px;
  transition: all 0.2s;
}
.nav-link:hover { color: var(--text-primary); background: var(--bg-hover); }
.router-link-active { color: var(--text-primary) !important; background: var(--bg-hover); }
.clock-text { font-family: var(--font-mono); font-size: 0.85rem; color: var(--text-secondary); }
.top-btn {
  background: transparent;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 1rem;
  padding: 0.2rem 0.4rem;
}
.top-btn:hover { color: var(--text-primary); }
</style>
