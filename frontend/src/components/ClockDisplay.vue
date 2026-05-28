<template>
  <div class="clock-display">
    <div class="time-label">{{ label }}</div>
    <div class="time-value">{{ displayTime }}</div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  label: string
  displayTime: string
}>()
</script>

<style scoped>
.clock-display {
  text-align: center;
}
.time-label {
  font-size: 1.2rem;
  opacity: 0.7;
  margin-bottom: 0.5rem;
  letter-spacing: 0.2em;
}
.time-value {
  font-family: var(--font-mono);
  font-size: 10rem;
  font-weight: 200;
  line-height: 1;
  text-shadow: 0 0 40px rgba(0,0,0,0.5);
}
</style>
