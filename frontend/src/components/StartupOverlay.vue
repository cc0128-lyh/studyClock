<template>
  <Transition name="startup-fade">
    <div v-if="visible" class="startup-overlay">
      <div class="startup-content">
        <div class="startup-icon">⏱️</div>
        <div class="startup-title">StudyClock</div>
        <div class="startup-dots">
          <span>正在启动</span>
          <span class="dot-anim"><span>.</span><span>.</span><span>.</span></span>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
defineProps<{ visible: boolean }>()
</script>

<style scoped>
.startup-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-primary, #1a1a2e);
}
.startup-content {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.6rem;
}
.startup-icon { font-size: 3.5rem; }
.startup-title {
  font-size: 1.5rem;
  font-weight: 300;
  letter-spacing: 0.15em;
  opacity: 0.8;
}
.startup-dots {
  font-size: 0.9rem;
  opacity: 0.5;
  display: flex;
  align-items: center;
  gap: 0.1rem;
  margin-top: 0.3rem;
}
.dot-anim { display: inline-flex; gap: 0.05rem; }
.dot-anim span {
  animation: dotPulse 1.4s ease-in-out infinite;
  font-weight: 600;
}
.dot-anim span:nth-child(2) { animation-delay: 0.2s; }
.dot-anim span:nth-child(3) { animation-delay: 0.4s; }

@keyframes dotPulse {
  0%, 80%, 100% { opacity: 0; transform: translateY(0); }
  40% { opacity: 1; transform: translateY(-3px); }
}

.startup-fade-leave-active {
  transition: opacity 0.5s ease;
}
.startup-fade-leave-to {
  opacity: 0;
}
</style>
