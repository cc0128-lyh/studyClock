<template>
  <div class="shortcut-panel">
    <div class="shortcut-title">快捷工具</div>
    <div class="shortcut-list">
      <button
        v-for="item in shortcuts"
        :key="item.id"
        class="shortcut-btn"
        @click="launch(item)"
      >
        <span class="shortcut-icon">
          <img v-if="item.icon && item.icon.startsWith('http')" :src="item.icon" alt="" class="shortcut-favicon" @error="onImgError" />
          <span v-else>{{ item.icon || '🔗' }}</span>
        </span>
        <span class="shortcut-name">{{ item.name }}</span>
      </button>
    </div>
    <button class="add-btn" @click="$router.push('/settings')">管理</button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import type { Shortcut } from '@/api/shortcut'

defineProps<{ shortcuts: Shortcut[] }>()
const router = useRouter()

function onImgError(e: Event) {
  const img = e.target as HTMLImageElement
  const fallback = document.createTextNode('🔗')
  img.replaceWith(fallback)
}

function launch(item: Shortcut) {
  if (window.electronAPI) {
    if (item.launchType === 'APP' && item.appPath) {
      window.electronAPI.launchApp(item.appPath)
    } else if (item.launchType === 'URL' && item.url) {
      window.electronAPI.launchUrl(item.url)
    }
  } else {
    if (item.url) {
      window.open(item.url, '_blank')
    }
  }
}
</script>

<style scoped>
.shortcut-panel {
  position: fixed;
  right: 1.5rem;
  top: 50%;
  transform: translateY(-50%);
  background: var(--panel-bg);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 1rem;
  min-width: 80px;
  border: 1px solid var(--border-color);
}
.shortcut-title {
  font-size: 0.75rem;
  text-align: center;
  opacity: 0.6;
  margin-bottom: 0.8rem;
  letter-spacing: 0.15em;
}
.shortcut-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.shortcut-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.2rem;
  padding: 0.6rem 0.4rem;
  background: var(--bg-hover);
  border: 1px solid transparent;
  border-radius: 10px;
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.2s;
  width: 64px;
}
.shortcut-btn:hover { background: var(--accent-muted); border-color: var(--border-color); }
.shortcut-icon { font-size: 1.3rem; display: flex; align-items: center; justify-content: center; }
.shortcut-favicon { width: 22px; height: 22px; border-radius: 4px; object-fit: contain; }
.shortcut-name { font-size: 0.7rem; opacity: 0.8; }
.add-btn {
  width: 100%;
  margin-top: 0.6rem;
  padding: 0.3rem;
  background: transparent;
  border: 1px dashed var(--border-color);
  border-radius: 8px;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 0.75rem;
}
.add-btn:hover { color: var(--text-primary); border-color: var(--border-light); }
</style>
