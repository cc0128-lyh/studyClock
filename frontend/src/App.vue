<template>
  <StartupOverlay :visible="showStartup" />
  <WallpaperBackground>
    <TopBar />
    <main class="main-content">
      <router-view />
    </main>
  </WallpaperBackground>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import StartupOverlay from '@/components/StartupOverlay.vue'
import WallpaperBackground from '@/components/WallpaperBackground.vue'
import TopBar from '@/components/TopBar.vue'
import { useThemeStore } from '@/stores/theme'
import api from '@/api'

const themeStore = useThemeStore()
const showStartup = ref(true)
let healthTimer: ReturnType<typeof setTimeout> | null = null

async function waitForBackend(retries = 30) {
  try {
    const res: any = await api.get('/health')
    if (res && res.data && res.data.status === 'UP') {
      showStartup.value = false
      return
    }
  } catch {
    // backend not ready yet
  }
  if (retries > 0) {
    healthTimer = setTimeout(() => waitForBackend(retries - 1), 1000)
  } else {
    // 超时后仍然显示界面，不 blocking
    showStartup.value = false
  }
}

onMounted(() => {
  themeStore.initTheme()
  waitForBackend()
})

onUnmounted(() => {
  if (healthTimer) clearTimeout(healthTimer)
})
</script>

<style scoped>
.main-content {
  position: relative;
  z-index: 1;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: 48px;
}
</style>
