<template>
  <div class="wallpaper-bg" :style="bgStyleComputed">
    <div class="wallpaper-content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useWallpaperStore } from '@/stores/wallpaper'
import { resolveBackendUrl } from '@/api'

const wallpaperStore = useWallpaperStore()

onMounted(() => {
  wallpaperStore.fetchActive()
})

const bgStyleComputed = computed(() => {
  const wp = wallpaperStore.activeWallpaper

  if (!wp) {
    return {}
  }
  if (wp.type === 'COLOR') {
    return { background: wp.bgColor || '' }
  }
  // IMAGE type
  return {
    background: `url(${resolveBackendUrl(wp.filePath)}) center/cover no-repeat`
  }
})
</script>

<style scoped>
.wallpaper-bg {
  width: 100%;
  height: 100%;
  position: relative;
  transition: background 0.8s ease;
  background: var(--bg-primary);
}
.wallpaper-bg::after {
  content: '';
  position: absolute;
  inset: 0;
  background: var(--wallpaper-overlay);
  pointer-events: none;
  z-index: 0;
}
.wallpaper-content {
  position: relative;
  z-index: 1;
  width: 100%;
  height: 100%;
}
</style>
