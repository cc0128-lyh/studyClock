<template>
  <div class="focus-detail-view">
    <!-- 返回按钮 -->
    <button class="back-btn" @click="goBack">← 返回</button>

    <!-- 滑动切换选项卡 -->
    <div class="tab-toggle">
      <div class="tab-track">
        <div class="tab-indicator" :style="tabIndicatorStyle"></div>
        <button
          v-for="t in tabs"
          :key="t.value"
          :class="['tab-btn', { active: activeTab === t.value }]"
          @click="activeTab = t.value"
        >
          {{ t.label }}
        </button>
      </div>
    </div>

    <!-- ========== 专注信息 ========== -->
    <template v-if="activeTab === 'focus'">
      <!-- 范围切换 -->
      <div class="range-toggle">
        <div class="toggle-track">
          <div class="toggle-indicator" :style="indicatorStyle"></div>
          <button
            v-for="opt in rangeOptions"
            :key="opt.value"
            :class="['toggle-btn', { active: range === opt.value }]"
            @click="range = opt.value"
          >
            {{ opt.label }}
          </button>
        </div>
      </div>

      <!-- 饼状图 -->
      <div class="chart-section">
        <PieChart
          :data="chartData"
          :total-label="totalLabel"
          size="300"
        />
      </div>

      <!-- 学科详情列表 -->
      <div v-if="breakdown.length > 0" class="detail-list">
        <div v-for="(item, i) in breakdown" :key="item.subjectName" class="detail-row">
          <div class="dr-left">
            <span class="dr-dot" :style="{ background: colors[i % colors.length] }"></span>
            <span class="dr-name">{{ item.subjectName }}</span>
          </div>
          <div class="dr-right">
            <span class="dr-time">{{ formatHours(item.totalSeconds) }}</span>
            <span class="dr-count">{{ item.sessionCount }} 次</span>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <p>该时段暂无专注记录</p>
      </div>
    </template>

    <!-- ========== 考试信息 ========== -->
    <template v-if="activeTab === 'exam'">
      <!-- 考试范围切换 -->
      <div class="range-toggle">
        <div class="toggle-track">
          <div class="toggle-indicator" :style="examRangeIndicatorStyle"></div>
          <button
            v-for="opt in examRangeOptions"
            :key="opt.value"
            :class="['toggle-btn', { active: examRange === opt.value }]"
            @click="examRange = opt.value"
          >
            {{ opt.label }}
          </button>
        </div>
      </div>

      <!-- 考试列表 -->
      <div v-if="!selectedExam" class="exam-list-wrap">
        <!-- 学科筛选下拉 -->
        <div class="exam-filter-bar">
          <div class="custom-select" ref="subjectSelectRef">
            <div class="custom-select-trigger" @click="showSubjectMenu = !showSubjectMenu">
              <span class="custom-select-value">{{ examSubjectFilter || '全部学科' }}</span>
              <span class="custom-select-arrow" :class="{ open: showSubjectMenu }"></span>
            </div>
            <Transition name="dropdown">
              <div v-if="showSubjectMenu" class="custom-select-menu">
                <button
                  class="custom-select-option"
                  :class="{ selected: !examSubjectFilter }"
                  @click="examSubjectFilter = ''; showSubjectMenu = false"
                >全部学科</button>
                <button
                  v-for="s in examSubjects"
                  :key="s"
                  class="custom-select-option"
                  :class="{ selected: examSubjectFilter === s }"
                  @click="examSubjectFilter = s; showSubjectMenu = false"
                >{{ s }}</button>
              </div>
            </Transition>
          </div>
        </div>

        <div v-if="groupedExams.length > 0" class="exam-list">
          <div v-for="group in groupedExams" :key="group.subject" class="exam-group">
            <div class="exam-group-header">{{ group.subject }}</div>
            <div
              v-for="exam in group.sessions"
              :key="exam.id"
              class="exam-row"
              @click="selectedExam = exam"
            >
              <div class="er-left">
                <span class="er-paper">{{ exam.examPaperName || '未命名试卷' }}</span>
                <span class="er-date">{{ formatDate(exam.startTime) }}</span>
              </div>
              <div class="er-right">
                <span class="er-score" v-if="exam.examScore != null">{{ exam.examScore }}<small>/{{ exam.examTotalScore || '?' }}</small></span>
                <span class="er-arrow">→</span>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <p>该时段暂无考试记录</p>
        </div>
      </div>

      <!-- 考试详情 -->
      <div v-else class="exam-detail">
        <button class="back-btn" @click="selectedExam = null">← 返回列表</button>

        <div class="exam-detail-card">
          <div class="ed-header">{{ selectedExam.examPaperName || '未命名试卷' }}</div>
          <div class="ed-subject">{{ selectedExam.subjectName || '其他' }}</div>

          <div class="ed-info-grid">
            <div class="ed-info-item">
              <span class="ed-label">考试时间</span>
              <span class="ed-value">{{ formatDate(selectedExam.startTime) }}</span>
            </div>
            <div class="ed-info-item">
              <span class="ed-label">用时</span>
              <span class="ed-value">{{ formatDuration(selectedExam.actualSeconds) }}</span>
            </div>
          </div>

          <div class="ed-scores">
            <div class="ed-score-item">
              <span class="ed-label">满分</span>
              <span class="ed-value score">{{ selectedExam.examTotalScore ?? '—' }}</span>
            </div>
            <div class="ed-score-item">
              <span class="ed-label">得分</span>
              <span class="ed-value score accent">{{ selectedExam.examScore ?? '—' }}</span>
            </div>
          </div>

          <div class="ed-section">
            <div class="ed-section-title">错题记录</div>
            <div class="ed-wrong-questions">{{ selectedExam.wrongQuestions || '无记录' }}</div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { statisticsApi, type SubjectBreakdownItem } from '@/api/statistics'
import { timerApi, type FocusSession } from '@/api/timer'
import PieChart from '@/components/PieChart.vue'

const router = useRouter()
const activeTab = ref('focus')

// Focus info state
const range = ref('WEEK')
const breakdown = ref<SubjectBreakdownItem[]>([])

// Exam info state
const examRange = ref('WEEK')
const examSessions = ref<FocusSession[]>([])
const selectedExam = ref<FocusSession | null>(null)
const examSubjectFilter = ref('')
const showSubjectMenu = ref(false)
const subjectSelectRef = ref<HTMLElement | null>(null)

const tabs = [
  { value: 'focus', label: '专注信息' },
  { value: 'exam', label: '考试信息' }
]

const rangeOptions = [
  { value: 'TODAY', label: '今日' },
  { value: 'WEEK', label: '本周' },
  { value: 'MONTH', label: '本月' },
  { value: 'TOTAL', label: '合计' }
]

const examRangeOptions = [
  { value: 'TODAY', label: '今日' },
  { value: 'WEEK', label: '本周' },
  { value: 'MONTH', label: '本月' },
  { value: 'TOTAL', label: '全部' }
]

const colors = [
  '#4fc3f7', '#ff8a65', '#81c784', '#ba68c8',
  '#ffd54f', '#4dd0e1', '#a1887f', '#90a4ae',
  '#e57373', '#7986cb'
]

const tabIndicatorStyle = computed(() => {
  const idx = tabs.findIndex(t => t.value === activeTab.value)
  const w = 100 / tabs.length
  return { left: `${idx * w}%`, width: `${w}%` }
})

const indicatorStyle = computed(() => {
  const idx = rangeOptions.findIndex(o => o.value === range.value)
  const w = 100 / rangeOptions.length
  return { left: `${idx * w}%`, width: `${w}%` }
})

const examRangeIndicatorStyle = computed(() => {
  const idx = examRangeOptions.findIndex(o => o.value === examRange.value)
  const w = 100 / examRangeOptions.length
  return { left: `${idx * w}%`, width: `${w}%` }
})

const chartData = computed(() =>
  breakdown.value.map(item => ({
    name: item.subjectName,
    value: item.totalSeconds
  }))
)

const totalSeconds = computed(() =>
  breakdown.value.reduce((s, item) => s + item.totalSeconds, 0)
)

const totalLabel = computed(() => formatHours(totalSeconds.value))

const groupedExams = computed(() => {
  let filtered = examSessions.value
  if (examSubjectFilter.value) {
    filtered = filtered.filter(s => (s.subjectName || '其他') === examSubjectFilter.value)
  }
  const groups: { subject: string; sessions: FocusSession[] }[] = []
  const map = new Map<string, FocusSession[]>()
  for (const s of filtered) {
    const subject = s.subjectName || '其他'
    if (!map.has(subject)) map.set(subject, [])
    map.get(subject)!.push(s)
  }
  for (const [subject, sessions] of map) {
    groups.push({ subject, sessions })
  }
  return groups
})

const examSubjects = computed(() => {
  const set = new Set<string>()
  for (const s of examSessions.value) {
    set.add(s.subjectName || '其他')
  }
  return [...set].sort()
})

function formatHours(seconds: number): string {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  return h > 0 ? `${h}h${m}m` : `${m}m`
}

function formatDuration(seconds: number | null | undefined): string {
  if (seconds == null) return '—'
  return formatHours(seconds)
}

function formatDate(iso: string | null | undefined): string {
  if (!iso) return '—'
  const d = new Date(iso)
  const month = d.getMonth() + 1
  const day = d.getDate()
  const hour = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  return `${month}月${day}日 ${hour}:${min}`
}

function goBack() {
  router.push('/statistics')
}

async function fetchBreakdown() {
  try {
    const res: any = await statisticsApi.subjectBreakdown(range.value)
    breakdown.value = res.data?.subjectBreakdown || []
  } catch (e) {
    console.error(e)
  }
}

async function fetchExamHistory() {
  try {
    const dateParam = examRange.value === 'TODAY'
      ? new Date().toISOString().split('T')[0]
      : undefined
    const res: any = await timerApi.examHistory(dateParam)
    let sessions: FocusSession[] = res.data || []
    // Filter by range on client side if no date param
    if (!dateParam) {
      const now = Date.now()
      const ranges: Record<string, number> = {
        WEEK: 7 * 86400000,
        MONTH: 30 * 86400000,
        TOTAL: Infinity
      }
      const limit = ranges[examRange.value] || ranges.WEEK
      sessions = sessions.filter(s => {
        const t = new Date(s.startTime).getTime()
        return (now - t) <= limit
      })
    }
    examSessions.value = sessions
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchBreakdown()
  fetchExamHistory()
  document.addEventListener('click', onDocumentClick)
})

onUnmounted(() => {
  document.removeEventListener('click', onDocumentClick)
})

function onDocumentClick(e: MouseEvent) {
  if (subjectSelectRef.value && !subjectSelectRef.value.contains(e.target as Node)) {
    showSubjectMenu.value = false
  }
}

watch(range, () => fetchBreakdown())
watch(examRange, () => { selectedExam.value = null; examSubjectFilter.value = ''; fetchExamHistory() })
</script>

<style scoped>
.focus-detail-view {
  max-width: 500px;
  margin: 0 auto;
  padding: 1.5rem;
}

/* Back button */
.back-btn {
  background: none;
  border: none;
  color: var(--text-secondary);
  font-size: 0.9rem;
  cursor: pointer;
  padding: 0.3rem 0;
  margin-bottom: 1.5rem;
  transition: color 0.2s;
}
.back-btn:hover { color: var(--text-primary); }

/* Tab toggle */
.tab-toggle {
  display: flex;
  justify-content: center;
  margin-bottom: 1.5rem;
}
.tab-track {
  position: relative;
  display: flex;
  background: var(--bg-hover);
  border-radius: 20px;
  padding: 3px;
  width: 220px;
}
.tab-indicator {
  position: absolute;
  top: 3px;
  bottom: 3px;
  background: var(--accent-muted);
  border-radius: 17px;
  transition: left 0.25s ease;
  z-index: 0;
}
.tab-btn {
  flex: 1;
  position: relative;
  z-index: 1;
  padding: 0.5rem 0;
  border: none;
  background: transparent;
  color: var(--text-secondary);
  font-size: 0.85rem;
  cursor: pointer;
  border-radius: 17px;
  transition: color 0.2s;
}
.tab-btn.active { color: var(--text-primary); }
.tab-btn:hover:not(.active) { color: var(--text-primary); }

/* Range toggle */
.range-toggle {
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
}
.toggle-track {
  position: relative;
  display: flex;
  background: var(--bg-hover);
  border-radius: 20px;
  padding: 3px;
  width: 260px;
}
.toggle-indicator {
  position: absolute;
  top: 3px;
  bottom: 3px;
  background: var(--accent-muted);
  border-radius: 17px;
  transition: left 0.25s ease;
  z-index: 0;
}
.toggle-btn {
  flex: 1;
  position: relative;
  z-index: 1;
  padding: 0.5rem 0;
  border: none;
  background: transparent;
  color: var(--text-secondary);
  font-size: 0.85rem;
  cursor: pointer;
  border-radius: 17px;
  transition: color 0.2s;
}
.toggle-btn.active { color: var(--text-primary); }
.toggle-btn:hover:not(.active) { color: var(--text-primary); }

/* Chart */
.chart-section {
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
}

/* Detail list (focus) */
.detail-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  max-height: 320px;
  overflow-y: auto;
  padding-right: 0.4rem;
}
.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.7rem 1rem;
  background: var(--bg-card);
  border-radius: 10px;
  transition: background 0.2s;
}
.detail-row:hover { background: var(--bg-hover); }
.dr-left { display: flex; align-items: center; gap: 0.6rem; }
.dr-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}
.dr-name { font-size: 0.9rem; }
.dr-right { display: flex; align-items: center; gap: 0.8rem; }
.dr-time { font-family: var(--font-mono); font-size: 0.85rem; }
.dr-count { font-size: 0.75rem; opacity: 0.4; }

/* Exam filter bar */
.exam-filter-bar {
  display: flex;
  justify-content: center;
  margin-bottom: 1rem;
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
  padding: 0.5rem 1rem;
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
  font-size: 0.85rem;
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
  font-size: 0.85rem;
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
.dropdown-enter-active { transition: all 0.2s ease-out; }
.dropdown-leave-active { transition: all 0.15s ease-in; }
.dropdown-enter-from { opacity: 0; transform: translateY(-6px); }
.dropdown-leave-to { opacity: 0; transform: translateY(-4px); }

/* Exam list */
.exam-list-wrap {
  max-height: 480px;
  overflow-y: auto;
  padding-right: 0.4rem;
}
.exam-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.exam-group {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}
.exam-group-header {
  font-size: 0.8rem;
  font-weight: 600;
  opacity: 0.6;
  padding: 0.3rem 0.5rem;
  letter-spacing: 0.05em;
}
.exam-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.7rem 1rem;
  background: var(--bg-card);
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s;
}
.exam-row:hover {
  background: var(--bg-hover);
}
.er-left {
  display: flex;
  flex-direction: column;
  gap: 0.15rem;
}
.er-paper {
  font-size: 0.9rem;
  font-weight: 500;
}
.er-date {
  font-size: 0.75rem;
  opacity: 0.45;
}
.er-right {
  display: flex;
  align-items: center;
  gap: 0.6rem;
}
.er-score {
  font-family: var(--font-mono);
  font-size: 1rem;
  font-weight: 500;
  color: var(--accent-color);
}
.er-score small {
  font-size: 0.7rem;
  opacity: 0.4;
  font-weight: 400;
}
.er-arrow {
  font-size: 0.9rem;
  opacity: 0.3;
}

/* Exam detail */
.exam-detail {
  animation: fadeIn 0.25s ease;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
.exam-detail-card {
  background: var(--panel-bg);
  border: 1px solid var(--border-color);
  border-radius: 18px;
  padding: 1.5rem;
}
.ed-header {
  font-size: 1.2rem;
  font-weight: 600;
  text-align: center;
  margin-bottom: 0.2rem;
}
.ed-subject {
  text-align: center;
  font-size: 0.82rem;
  opacity: 0.5;
  margin-bottom: 1.5rem;
}
.ed-info-grid {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.2rem;
}
.ed-info-item {
  flex: 1;
  background: var(--bg-hover);
  border-radius: 12px;
  padding: 0.8rem;
  text-align: center;
}
.ed-scores {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.2rem;
}
.ed-score-item {
  flex: 1;
  background: var(--bg-hover);
  border-radius: 12px;
  padding: 0.8rem;
  text-align: center;
}
.ed-label {
  display: block;
  font-size: 0.75rem;
  opacity: 0.5;
  margin-bottom: 0.3rem;
}
.ed-value {
  font-size: 0.95rem;
  font-weight: 500;
}
.ed-value.score {
  font-size: 1.4rem;
  font-weight: 300;
  font-family: var(--font-mono);
}
.ed-value.score.accent {
  color: var(--accent-color);
}
.ed-section {
  margin-top: 0.5rem;
}
.ed-section-title {
  font-size: 0.8rem;
  font-weight: 600;
  opacity: 0.6;
  margin-bottom: 0.5rem;
}
.ed-wrong-questions {
  font-size: 0.88rem;
  line-height: 1.7;
  background: var(--bg-hover);
  border-radius: 10px;
  padding: 0.8rem 1rem;
  white-space: pre-wrap;
  opacity: 0.85;
}

.empty-state {
  text-align: center;
  padding: 3rem 0;
  opacity: 0.4;
  font-size: 0.9rem;
}
</style>
