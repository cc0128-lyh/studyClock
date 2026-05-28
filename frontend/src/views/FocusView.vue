<template>
  <div :class="['focus-view', `pos-${focusStyle.style.position}`]">
    <!-- top progress bar -->
    <Transition name="slide">
      <div v-if="timerStore.currentSession && timerStore.examPhase !== 'review'" class="top-progress-bar">
        <div class="top-progress-subject">{{ timerStore.selectedSubject || '其他' }}</div>
        <div v-if="!timerStore.isCountup" class="top-progress-track">
          <div class="top-progress-fill" :style="{ width: `${timerStore.progress * 100}%` }"></div>
        </div>
      </div>
    </Transition>

    <!-- Normal content (hidden during review) -->
    <div v-if="timerStore.examPhase !== 'review'" class="center-content">
      <ClockDisplay
        :label="timerLabel"
        :display-time="timerStore.formattedTime"
      />
      <div class="timer-col">
        <TimerControls />

        <!-- Exam mode entry -->
        <div v-if="!timerStore.currentSession && !timerStore.examMode" class="exam-entry">
          <button class="exam-btn" @click="showExamInput = true">📝 考试模式</button>
        </div>
      </div>

      <!-- Exam setup dialog (frosted glass) -->
      <Transition name="fade">
        <div v-if="showExamInput" class="exam-setup">
          <div class="glass-card">
            <div class="glass-title">📝 考试设置</div>

            <div class="glass-row">
              <span class="glass-label">科目</span>
              <span class="glass-value">{{ timerStore.selectedSubject || '其他' }}</span>
            </div>

            <div class="glass-row">
              <span class="glass-label">试卷名称</span>
              <input
                v-model="examPaperName"
                type="text"
                class="glass-input"
                placeholder="输入试卷名称（可选）"
              />
            </div>

            <div class="glass-row">
              <span class="glass-label">考试时长</span>
              <div class="glass-input-group">
                <input
                  v-model.number="examInputMinutes"
                  type="number"
                  min="1"
                  max="600"
                  class="glass-input narrow"
                  placeholder="分钟"
                  @keyup.enter="startExam"
                />
                <span class="glass-unit">分钟</span>
              </div>
            </div>

            <div class="glass-actions">
              <button class="glass-btn primary" @click="startExam">开始考试</button>
              <button class="glass-btn secondary" @click="cancelExamSetup">取消</button>
            </div>
          </div>
        </div>
      </Transition>
    </div>

    <!-- Review page (full page during review phase) -->
    <div v-else class="review-page">
      <div class="review-header">
        <span class="review-header-icon">📋</span>
        <span class="review-header-title">考试复盘</span>
      </div>

      <div class="review-timer-section">
        <div class="review-timer-label">复盘时间</div>
        <div class="review-timer-value">{{ timerStore.reviewFormattedTime }}</div>
      </div>

      <div class="review-info-grid">
        <div class="review-info-item">
          <span class="review-info-label">科目</span>
          <span class="review-info-value">{{ timerStore.selectedSubject || '其他' }}</span>
        </div>
        <div class="review-info-item">
          <span class="review-info-label">试卷</span>
          <span class="review-info-value">{{ timerStore.examPaperName || '未命名' }}</span>
        </div>
      </div>

      <div class="review-field">
        <label class="review-field-label">错题记录</label>
        <textarea
          v-model="timerStore.wrongQuestions"
          class="review-textarea"
          placeholder="记录本次考试的错题..."
          rows="4"
        ></textarea>
      </div>

      <div class="review-scores">
        <div class="review-score-field">
          <label class="review-field-label">考试满分</label>
          <div class="review-score-input-wrap">
            <input
              v-model.number="timerStore.examTotalScore"
              type="number"
              min="0"
              class="review-input"
            />
            <span class="review-unit">分</span>
          </div>
        </div>
        <div class="review-score-field">
          <label class="review-field-label">考试得分</label>
          <div class="review-score-input-wrap">
            <input
              v-model.number="timerStore.examScore"
              type="number"
              min="0"
              class="review-input"
            />
            <span class="review-unit">分</span>
          </div>
        </div>
      </div>

      <div class="review-actions">
        <button class="review-btn primary" @click="timerStore.confirmReview()">确认完成</button>
        <button class="review-btn secondary" @click="timerStore.cancelReview()">跳过复盘</button>
      </div>
    </div>

    <ShortcutPanel
      v-if="!timerStore.isExam"
      :shortcuts="shortcuts"
    />

    <BreakReminder
      v-if="timerStore.examPhase !== 'review'"
      :visible="timerStore.justCompleted"
      :minutes="timerStore.completedMinutes"
      :seconds="timerStore.completedSeconds"
      :exam-mode="timerStore.completedAsExam"
      @close="timerStore.acknowledgeCompletion()"
      @start-next="startNextRound"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import ClockDisplay from '@/components/ClockDisplay.vue'
import TimerControls from '@/components/TimerControls.vue'
import ShortcutPanel from '@/components/ShortcutPanel.vue'
import BreakReminder from '@/components/BreakReminder.vue'
import { useTimerStore } from '@/stores/timer'
import { useFocusStyleStore } from '@/stores/focusStyle'
import { shortcutApi, type Shortcut } from '@/api/shortcut'

const timerStore = useTimerStore()
const focusStyle = useFocusStyleStore()

const timerLabel = computed(() => {
  if (timerStore.isExam) return '考试模式'
  return timerStore.isCountup ? '已专注' : '专注时间'
})
const shortcuts = ref<Shortcut[]>([])

const showExamInput = ref(false)
const examInputMinutes = ref(120)
const examPaperName = ref('')

async function startExam() {
  const m = Math.max(1, Math.min(600, examInputMinutes.value || 120))
  showExamInput.value = false
  timerStore.enterExamMode(m)
  timerStore.examPaperName = examPaperName.value
  try {
    await timerStore.startSession(timerStore.selectedSubject || undefined)
    if (timerStore.currentSession) {
      timerStore.examPhase = 'exam'
    }
  } catch {}
}

function cancelExamSetup() {
  showExamInput.value = false
  examInputMinutes.value = 120
  examPaperName.value = ''
}

function startNextRound() {
  timerStore.acknowledgeCompletion()
  timerStore.startSession(timerStore.selectedSubject || undefined)
}

async function fetchShortcuts() {
  try {
    const res: any = await shortcutApi.list()
    shortcuts.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchShortcuts()
  timerStore.loadDefaultTarget()
  timerStore.loadNotificationSetting()
})
</script>

<style scoped>
.focus-view {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: all 0.5s ease;
}
.center-content { text-align: center; }
.timer-col {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
}

/* --- Position presets --- */

/* Center (default) */
.pos-center .center-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* Top compact */
.pos-top {
  justify-content: flex-start;
  padding-top: 2rem;
}
.pos-top .center-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* Side float */
.pos-side {
  flex-direction: row;
  justify-content: center;
  gap: 4rem;
}
.pos-side .center-content {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 3rem;
}

/* Top progress bar */
.top-progress-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1;
}
.top-progress-subject {
  text-align: center;
  font-size: 0.9rem;
  font-weight: 500;
  padding: 0.4rem 0 0.3rem;
  opacity: 0.6;
  letter-spacing: 0.15em;
  line-height: 1.2;
}
.top-progress-track {
  width: 100%;
  height: 4px;
  background: var(--border-light);
}
.top-progress-fill {
  height: 100%;
  background: var(--accent-color);
  transition: width 1s linear;
}

/* Progress bar slide transition */
.slide-enter-active {
  transition: all 0.4s ease-out;
}
.slide-leave-active {
  transition: all 0.3s ease-in;
}
.slide-enter-from {
  transform: translateY(-100%);
  opacity: 0;
}
.slide-leave-to {
  transform: translateY(-100%);
  opacity: 0;
}

/* Exam mode entry */
.exam-entry {
  margin-top: 1.5rem;
}
.exam-btn {
  padding: 0.5rem 1.5rem;
  background: transparent;
  border: 1px solid var(--accent-color);
  border-radius: 20px;
  color: var(--accent-color);
  cursor: pointer;
  font-size: 0.85rem;
  letter-spacing: 0.05em;
  transition: all 0.2s;
}
.exam-btn:hover {
  background: var(--accent-color);
  color: var(--text-inverse);
}

/* --- Frosted glass overlay (exam setup dialog) --- */
.exam-setup {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.5);
  z-index: 100;
}

.glass-card {
  background: var(--panel-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255,255,255,0.15);
  border-radius: 24px;
  padding: 2rem 2.2rem;
  text-align: left;
  min-width: 320px;
  max-width: 420px;
  width: 90%;
  box-shadow: 0 8px 40px rgba(0,0,0,0.3);
  animation: glassIn 0.3s ease;
}
@keyframes glassIn {
  from { transform: translateY(20px) scale(0.97); opacity: 0; }
  to { transform: translateY(0) scale(1); opacity: 1; }
}

.glass-title {
  font-size: 1.15rem;
  font-weight: 600;
  text-align: center;
  margin-bottom: 1.5rem;
  letter-spacing: 0.05em;
}

.glass-row {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  margin-bottom: 1rem;
  padding: 0 0.2rem;
}
.glass-label {
  font-size: 0.82rem;
  opacity: 0.6;
  min-width: 4.5em;
  flex-shrink: 0;
}
.glass-value {
  font-size: 0.95rem;
  font-weight: 500;
}

.glass-input {
  flex: 1;
  padding: 0.55rem 0.8rem;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 10px;
  color: var(--text-primary);
  font-size: 0.92rem;
  outline: none;
  transition: border-color 0.2s;
  min-width: 0;
}
.glass-input:focus {
  border-color: var(--accent-color);
}
.glass-input.narrow {
  flex: none;
  width: 90px;
  text-align: center;
}
.glass-input::placeholder {
  color: var(--text-muted);
  opacity: 0.5;
}
.glass-input::-webkit-outer-spin-button,
.glass-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.glass-input-group {
  display: flex;
  align-items: center;
  gap: 0.4rem;
}
.glass-unit {
  font-size: 0.82rem;
  opacity: 0.5;
}

.glass-actions {
  display: flex;
  gap: 0.8rem;
  justify-content: center;
  margin-top: 1.5rem;
}
.glass-btn {
  padding: 0.6rem 1.8rem;
  border: none;
  border-radius: 20px;
  font-size: 0.92rem;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
  letter-spacing: 0.03em;
}
.glass-btn.primary {
  background: var(--accent-color);
  color: var(--text-inverse);
}
.glass-btn.primary:hover {
  opacity: 0.85;
}
.glass-btn.secondary {
  background: rgba(255,255,255,0.08);
  color: var(--text-secondary);
  border: 1px solid rgba(255,255,255,0.1);
}
.glass-btn.secondary:hover {
  background: rgba(255,255,255,0.14);
  color: var(--text-primary);
}

/* --- Review page --- */
.review-page {
  width: 100%;
  max-width: 480px;
  padding: 2rem 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: reviewIn 0.35s ease;
}
@keyframes reviewIn {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}

.review-header {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  margin-bottom: 2rem;
}
.review-header-icon {
  font-size: 1.6rem;
}
.review-header-title {
  font-size: 1.3rem;
  font-weight: 600;
  letter-spacing: 0.05em;
}

.review-timer-section {
  text-align: center;
  margin-bottom: 2rem;
}
.review-timer-label {
  font-size: 0.82rem;
  opacity: 0.5;
  margin-bottom: 0.3rem;
}
.review-timer-value {
  font-family: var(--font-mono, monospace);
  font-size: 3rem;
  font-weight: 200;
  color: var(--accent-color);
  letter-spacing: 0.05em;
  line-height: 1.2;
}

.review-info-grid {
  display: flex;
  gap: 2rem;
  margin-bottom: 1.5rem;
  padding: 0.8rem 1.2rem;
  background: var(--bg-hover);
  border-radius: 12px;
  width: 100%;
}
.review-info-item {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  flex: 1;
  text-align: center;
}
.review-info-label {
  font-size: 0.75rem;
  opacity: 0.5;
}
.review-info-value {
  font-size: 0.95rem;
  font-weight: 500;
}

.review-field {
  width: 100%;
  margin-bottom: 1.2rem;
}
.review-field-label {
  display: block;
  font-size: 0.8rem;
  opacity: 0.6;
  margin-bottom: 0.4rem;
}
.review-textarea {
  width: 100%;
  padding: 0.7rem 0.9rem;
  background: var(--bg-hover);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  color: var(--text-primary);
  font-size: 0.9rem;
  outline: none;
  resize: vertical;
  min-height: 80px;
  font-family: inherit;
  line-height: 1.6;
  box-sizing: border-box;
  transition: border-color 0.2s;
}
.review-textarea:focus {
  border-color: var(--accent-color);
}
.review-textarea::placeholder {
  color: var(--text-muted);
  opacity: 0.5;
}

.review-scores {
  display: flex;
  gap: 1rem;
  width: 100%;
  margin-bottom: 2rem;
}
.review-score-field {
  flex: 1;
}
.review-score-input-wrap {
  display: flex;
  align-items: center;
  gap: 0.4rem;
}
.review-input {
  width: 100%;
  padding: 0.6rem 0.8rem;
  background: var(--bg-hover);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  color: var(--text-primary);
  font-size: 1.1rem;
  text-align: center;
  outline: none;
  box-sizing: border-box;
  transition: border-color 0.2s;
}
.review-input:focus {
  border-color: var(--accent-color);
}
.review-input::-webkit-outer-spin-button,
.review-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
.review-unit {
  font-size: 0.82rem;
  opacity: 0.5;
  flex-shrink: 0;
}

.review-actions {
  display: flex;
  gap: 0.8rem;
  justify-content: center;
}
.review-btn {
  padding: 0.7rem 2rem;
  border: none;
  border-radius: 24px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
  letter-spacing: 0.04em;
}
.review-btn.primary {
  background: var(--accent-color);
  color: var(--text-inverse);
}
.review-btn.primary:hover {
  opacity: 0.85;
}
.review-btn.secondary {
  background: transparent;
  color: var(--text-muted);
  border: 1px solid var(--border-color);
}
.review-btn.secondary:hover {
  color: var(--text-primary);
  border-color: var(--text-secondary);
}

/* Fade transition for overlay */
.fade-enter-active { transition: opacity 0.25s ease; }
.fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from,
.fade-leave-to { opacity: 0; }
</style>
