<template>
  <div class="wallpaper-bg" :style="bgStyleComputed">
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useWallpaperStore } from '@/stores/wallpaper'

const wallpaperStore = useWallpaperStore()

onMounted(() => {
  wallpaperStore.fetchActive()
})

const bgStyleComputed = computed(() => {
  const wp = wallpaperStore.activeWallpaper
  const overlay = 'linear-gradient(rgba(0,0,0,0.3), rgba(0,0,0,0.3))'

  if (!wp) {
    return { background: '#1a1a2e' }
  }
  if (wp.type === 'COLOR') {
    return { background: wp.bgColor || '#1a1a2e' }
  }
  // IMAGE type: overlay gradient + image
  return {
    background: `${overlay}, url(${wp.filePath}) center/cover no-repeat`
  }
})
</script>

<style scoped>
.wallpaper-bg {
  width: 100%;
  height: 100%;
  position: relative;
  transition: background 0.8s ease;
}
</style>
