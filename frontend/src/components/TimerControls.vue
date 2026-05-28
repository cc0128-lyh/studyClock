<template>
  <div class="timer-controls">
    <Transition name="fade">
      <div v-if="!timerStore.isRunning && !timerStore.currentSession && !timerStore.isCountup && !timerStore.isExam" class="presets">
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
    </Transition>

    <!-- subject selector -->
    <Transition name="fade">
      <div v-if="!timerStore.currentSession && subjectStore.list.length > 0" class="subject-selector">
        <span class="subject-label">学科</span>
        <div class="custom-select" ref="selectRef">
          <div class="custom-select-trigger" @click="toggleSubjectMenu">
            <span class="custom-select-value">{{ timerStore.selectedSubject || '其他' }}</span>
            <span class="custom-select-arrow" :class="{ open: showSubjectMenu }"></span>
          </div>
          <Transition name="dropdown">
            <div v-if="showSubjectMenu" class="custom-select-menu">
              <button
                class="custom-select-option"
                :class="{ selected: !timerStore.selectedSubject }"
                @click="selectSubject('')"
              >其他</button>
              <button
                v-for="s in subjectStore.list"
                :key="s.id"
                class="custom-select-option"
                :class="{ selected: timerStore.selectedSubject === s.name }"
                @click="selectSubject(s.name)"
              >{{ s.name }}</button>
            </div>
          </Transition>
        </div>
      </div>
    </Transition>

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

const showSubjectMenu = ref(false)
const selectRef = ref<HTMLElement | null>(null)

function toggleSubjectMenu() {
  showSubjectMenu.value = !showSubjectMenu.value
}
function selectSubject(name: string) {
  timerStore.selectedSubject = name
  showSubjectMenu.value = false
}
function onDocumentClick(e: MouseEvent) {
  if (selectRef.value && !selectRef.value.contains(e.target as Node)) {
    showSubjectMenu.value = false
  }
}
watch(showSubjectMenu, (val) => {
  if (val) {
    nextTick(() => document.addEventListener('click', onDocumentClick))
  } else {
    document.removeEventListener('click', onDocumentClick)
  }
})

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
  border: 1px solid var(--border-subtle);
  border-radius: 20px;
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}
.preset-btn:hover { background: var(--bg-hover); color: var(--text-primary); }
.preset-btn.active { background: var(--accent-color); color: var(--text-inverse); border-color: var(--accent-color); }

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
  background: var(--bg-hover);
  border: 1px solid var(--border-subtle);
  border-radius: 6px;
  color: var(--text-primary);
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
  align-items: center;
  justify-content: center;
  gap: 0.6rem;
  margin-bottom: 1.5rem;
}
.subject-label {
  font-size: 0.8rem;
  color: var(--text-muted);
  letter-spacing: 0.1em;
}
.custom-select {
  position: relative;
  min-width: 150px;
}
.custom-select-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.55rem 1rem;
  background: var(--bg-hover);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.25s ease;
  user-select: none;
}
.custom-select-trigger:hover {
  background: var(--bg-card);
  border-color: var(--border-light);
}
.custom-select-value {
  font-size: 0.92rem;
  font-weight: 500;
  color: var(--text-primary);
}
.custom-select-arrow {
  display: inline-block;
  width: 0;
  height: 0;
  border-left: 4.5px solid transparent;
  border-right: 4.5px solid transparent;
  border-top: 5px solid var(--text-muted);
  transition: transform 0.25s, border-top-color 0.25s;
  flex-shrink: 0;
}
.custom-select-arrow.open {
  transform: rotate(180deg);
}
.custom-select-menu {
  position: absolute;
  top: calc(100% + 6px);
  left: 0;
  right: 0;
  background: var(--panel-bg);
  backdrop-filter: blur(16px);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  padding: 0.4rem;
  z-index: 20;
  overflow: hidden;
}
.custom-select-option {
  display: block;
  width: 100%;
  padding: 0.55rem 0.8rem;
  background: transparent;
  border: none;
  border-radius: 7px;
  color: var(--text-primary);
  font-size: 0.9rem;
  cursor: pointer;
  text-align: center;
  transition: background 0.15s;
}
.custom-select-option:hover {
  background: var(--bg-hover);
}
.custom-select-option.selected {
  background: var(--accent-color);
  color: var(--text-inverse);
  font-weight: 500;
}
/* Dropdown pop transition */
.dropdown-enter-active {
  transition: all 0.2s ease-out;
}
.dropdown-leave-active {
  transition: all 0.15s ease-in;
}
.dropdown-enter-from {
  opacity: 0;
  transform: translateY(-6px);
}
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

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
.action-btn.start, .action-btn.resume { background: var(--accent-color); color: var(--text-inverse); }
.action-btn.start:hover, .action-btn.resume:hover { opacity: 0.85; }
.action-btn.pause { background: var(--bg-hover); color: var(--text-primary); }
.action-btn.cancel { background: transparent; color: var(--text-muted); border: 1px solid var(--accent-muted); }
.action-btn.cancel:hover { border-color: var(--text-secondary); color: var(--text-primary); }

/* Fade transition */
.fade-enter-active {
  transition: all 0.35s ease-out;
}
.fade-leave-active {
  transition: all 0.25s ease-in;
}
.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
