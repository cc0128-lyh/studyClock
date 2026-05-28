<template>
  <WallpaperBackground>
    <TopBar />
    <main class="main-content">
      <router-view />
    </main>
  </WallpaperBackground>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import WallpaperBackground from '@/components/WallpaperBackground.vue'
import TopBar from '@/components/TopBar.vue'
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()

onMounted(() => {
  themeStore.initTheme()
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
