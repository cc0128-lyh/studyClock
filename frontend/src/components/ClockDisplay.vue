<template>
  <div :class="['clock-display', `pos-${focusStyle.style.position}`]">
    <div class="time-label">{{ label }}</div>
    <div class="time-value" :style="timeStyle">{{ displayTime }}</div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useFocusStyleStore } from '@/stores/focusStyle'

defineProps<{
  label: string
  displayTime: string
}>()

const focusStyle = useFocusStyleStore()

const defaultSizeByPos: Record<string, number> = {
  center: 10,
  top: 5,
  side: 6
}

const timeStyle = computed(() => {
  const s: Record<string, string> = {}
  if (focusStyle.style.fontFamily) s.fontFamily = focusStyle.style.fontFamily
  // Use custom size if set, otherwise use position default
  const size = focusStyle.style.fontSize || defaultSizeByPos[focusStyle.style.position] || 10
  s.fontSize = size + 'rem'
  if (focusStyle.style.color) s.color = focusStyle.style.color
  return s
})
</script>

<style scoped>
.clock-display {
  text-align: center;
  transition: all 0.5s ease;
}
.time-label {
  font-size: 1.2rem;
  opacity: 0.7;
  margin-bottom: 0.5rem;
  letter-spacing: 0.2em;
  transition: all 0.3s ease;
}
.time-value {
  font-family: var(--font-mono);
  font-size: 10rem;
  font-weight: 200;
  line-height: 1;
  text-shadow: 0 0 40px rgba(0,0,0,0.5);
  transition: all 0.5s ease;
}

/* Position tweaks */
.pos-top .time-label { font-size: 0.9rem; margin-bottom: 0.2rem; }
</style>
