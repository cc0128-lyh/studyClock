<template>
  <div class="timer-controls">
    <div v-if="!timerStore.isRunning && !timerStore.currentSession" class="presets">
      <button
        v-for="m in [5, 15, 25, 45, 60]"
        :key="m"
        :class="['preset-btn', { active: timerStore.targetMinutes === m && !showCustom }]"
        @click="selectPreset(m)"
      >
        {{ m }}分
      </button>
      <div class="custom-wrapper">
        <button
          :class="['preset-btn', 'custom-btn', { active: showCustom }]"
          @click="toggleCustom"
        >
          自定义
        </button>
        <div v-if="showCustom" class="custom-input-area">
          <input
            ref="customInput"
            v-model="customMinutes"
            type="number"
            min="0"
            max="999"
            class="custom-input"
            placeholder="分"
            @keyup.enter="applyCustom"
            @blur="applyCustom"
          />
          <span class="custom-unit">分</span>
          <input
            v-model="customSeconds"
            type="number"
            min="0"
            max="59"
            class="custom-input sec-input"
            placeholder="秒"
            @keyup.enter="applyCustom"
            @blur="applyCustom"
          />
          <span class="custom-unit">秒</span>
        </div>
      </div>
    </div>

    <!-- subject selector -->
    <div v-if="!timerStore.currentSession && subjectStore.list.length > 0" class="subject-selector">
      <select v-model="timerStore.selectedSubject" class="subject-dropdown">
        <option value="">其他</option>
        <option v-for="s in subjectStore.list" :key="s.id" :value="s.name">{{ s.name }}</option>
      </select>
    </div>

    <div class="actions">
      <button v-if="!timerStore.currentSession" class="action-btn start" @click="handleStart">
        开始专注
      </button>
      <button v-else-if="timerStore.isRunning" class="action-btn pause" @click="timerStore.pauseSession()">
        暂停
      </button>
      <button v-else class="action-btn resume" @click="timerStore.resumeSession()">
        继续
      </button>
      <button
        v-if="timerStore.currentSession"
        class="action-btn cancel"
        @click="timerStore.cancelSession()"
      >
        结束
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted } from 'vue'
import { useTimerStore } from '@/stores/timer'
import { useSubjectStore } from '@/stores/subject'

const timerStore = useTimerStore()
const subjectStore = useSubjectStore()

const showCustom = ref(false)
const customMinutes = ref(25)
const customSeconds = ref(0)
const customInput = ref<HTMLInputElement | null>(null)

onMounted(() => {
  subjectStore.fetchList()
})

function selectPreset(m: number) {
  showCustom.value = false
  timerStore.setTarget(m)
}

function toggleCustom() {
  showCustom.value = !showCustom.value
  if (showCustom.value) {
    customMinutes.value = timerStore.targetMinutes
    customSeconds.value = timerStore.targetSeconds
    nextTick(() => customInput.value?.focus())
  }
}

function applyCustom() {
  const m = Math.max(0, parseInt(String(customMinutes.value)) || 0)
  const s = Math.max(0, Math.min(59, parseInt(String(customSeconds.value)) || 0))
  if (m > 0 || s > 0) {
    timerStore.setCustomTarget(m, s)
  }
}

// 自定义输入变化时自动应用，确保 store 值始终同步
watch([customMinutes, customSeconds], () => {
  if (showCustom.value) applyCustom()
})

// 开始前如果处于自定义模式，先确保值已应用
function handleStart() {
  if (showCustom.value) applyCustom()
  timerStore.startSession(timerStore.selectedSubject || undefined)
}
</script>

<style scoped>
.timer-controls {
  text-align: center;
}
.presets {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}
.preset-btn {
  padding: 0.5rem 1.2rem;
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 20px;
  background: transparent;
  color: rgba(255,255,255,0.7);
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}
.preset-btn:hover { background: rgba(255,255,255,0.08); color: #fff; }
.preset-btn.active { background: #fff; color: #0a0a0a; border-color: #fff; }

.custom-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}
.custom-btn {
  font-size: 0.85rem;
}
.custom-input-area {
  position: absolute;
  left: 50%;
  top: calc(100% + 8px);
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 0.3rem;
  background: var(--panel-bg);
  backdrop-filter: blur(12px);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  padding: 0.4rem 0.6rem;
  z-index: 10;
  white-space: nowrap;
}
.custom-input {
  width: 70px;
  padding: 0.3rem 0.5rem;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 6px;
  color: #fff;
  font-size: 0.95rem;
  text-align: center;
  outline: none;
}
.custom-input.sec-input { width: 55px; }
.custom-input:focus { border-color: var(--accent-color); }
.custom-input::-webkit-outer-spin-button,
.custom-input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }
.custom-unit { font-size: 0.8rem; opacity: 0.6; }

.subject-selector {
  display: flex;
  justify-content: center;
  margin-bottom: 1.5rem;
}
.subject-dropdown {
  padding: 0.5rem 2rem 0.5rem 1rem;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 10px;
  color: #fff;
  font-size: 0.9rem;
  outline: none;
  cursor: pointer;
  appearance: none;
  -webkit-appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='rgba(255,255,255,0.5)' d='M6 8L1 3h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.6rem center;
  min-width: 160px;
  text-align: center;
  text-align-last: center;
}
.subject-dropdown:focus { border-color: var(--accent-color); }
.subject-dropdown option { background: #141414; color: #fff; }

.actions { display: flex; gap: 1rem; justify-content: center; }
.action-btn {
  padding: 0.8rem 2.5rem;
  border: none;
  border-radius: 30px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.2s;
  letter-spacing: 0.1em;
}
.action-btn.start, .action-btn.resume { background: #fff; color: #0a0a0a; }
.action-btn.start:hover, .action-btn.resume:hover { opacity: 0.85; }
.action-btn.pause { background: rgba(255,255,255,0.12); color: #fff; }
.action-btn.cancel { background: transparent; color: rgba(255,255,255,0.5); border: 1px solid rgba(255,255,255,0.15); }
.action-btn.cancel:hover { border-color: rgba(255,255,255,0.4); color: #fff; }
</style>
