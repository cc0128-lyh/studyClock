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
  const overlay = 'linear-gradient(rgba(10,10,10,0.5), rgba(10,10,10,0.7))'

  if (!wp) {
    return { background: '#0a0a0a' }
  }
  if (wp.type === 'COLOR') {
    return { background: wp.bgColor || '#0a0a0a' }
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
