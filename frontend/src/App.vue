<template>
  <WallpaperBackground>
    <TopBar />
    <main class="main-content">
      <router-view />
    </main>
  </WallpaperBackground>
</template>

<script setup lang="ts">
import WallpaperBackground from '@/components/WallpaperBackground.vue'
import TopBar from '@/components/TopBar.vue'
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
