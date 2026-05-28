<template>
  <Teleport to="body">
    <div v-if="visible" class="reminder-overlay" @click.self="close">
      <div class="reminder-card">
        <div class="reminder-icon">🎉</div>
        <div class="reminder-title">专注完成！</div>
        <div class="reminder-duration">
          本次专注
          <strong>{{ displayText }}</strong>
        </div>
        <div class="reminder-quote">
          <div class="quote-icon">💬</div>
          <p class="quote-text">{{ quote }}</p>
        </div>
        <div class="reminder-actions">
          <button class="action-btn primary" @click="close">休息一下</button>
          <button class="action-btn secondary" @click="startNext">开始下一轮</button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { quoteApi } from '@/api/quote'

const props = defineProps<{
  visible: boolean
  minutes: number
  seconds?: number
}>()

const emit = defineEmits<{
  close: []
  startNext: []
}>()

const quote = ref('')

const displayText = computed(() => {
  const parts: string[] = []
  if (props.minutes > 0) parts.push(`${props.minutes} 分`)
  if (props.seconds) parts.push(`${props.seconds} 秒`)
  return parts.join(' ') || '0 秒'
})

async function loadQuote() {
  try {
    const res: any = await quoteApi.random()
    quote.value = res.data?.content || '休息一下，继续前进！'
  } catch {
    quote.value = '休息一下，继续前进！'
  }
}

watch(() => props.visible, (v) => {
  if (v) loadQuote()
})

function close() {
  emit('close')
}

function startNext() {
  emit('startNext')
}
</script>

<style scoped>
.reminder-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.65);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.3s ease;
}
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
.reminder-card {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 24px;
  padding: 2.5rem 3rem;
  text-align: center;
  max-width: 440px;
  width: 90%;
  animation: slideUp 0.35s ease;
}
@keyframes slideUp {
  from { transform: translateY(30px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}
.reminder-icon { font-size: 3rem; margin-bottom: 0.5rem; }
.reminder-title {
  font-size: 1.6rem;
  font-weight: 600;
  margin-bottom: 0.3rem;
}
.reminder-duration {
  font-size: 0.95rem;
  opacity: 0.65;
  margin-bottom: 1.5rem;
}
.reminder-quote {
  background: rgba(255,255,255,0.05);
  border-radius: 14px;
  padding: 1.2rem;
  margin-bottom: 1.8rem;
  border: 1px solid rgba(255,255,255,0.06);
}
.quote-icon { font-size: 1.2rem; margin-bottom: 0.4rem; }
.quote-text {
  font-size: 1rem;
  line-height: 1.6;
  opacity: 0.9;
  font-style: italic;
  margin: 0;
}
.reminder-actions {
  display: flex;
  gap: 0.8rem;
  justify-content: center;
}
.action-btn {
  padding: 0.7rem 1.8rem;
  border: none;
  border-radius: 30px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s;
}
.action-btn.primary {
  background: var(--accent-color);
  color: #fff;
}
.action-btn.primary:hover { opacity: 0.85; }
.action-btn.secondary {
  background: rgba(255,255,255,0.08);
  color: rgba(255,255,255,0.8);
  border: 1px solid rgba(255,255,255,0.15);
}
.action-btn.secondary:hover { background: rgba(255,255,255,0.15); }
</style>
