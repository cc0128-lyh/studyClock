<template>
  <div class="focus-detail-view">
    <!-- 返回按钮 -->
    <button class="back-btn" @click="goBack">← 返回</button>

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
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { statisticsApi, type SubjectBreakdownItem } from '@/api/statistics'
import PieChart from '@/components/PieChart.vue'

const router = useRouter()
const range = ref('WEEK')
const breakdown = ref<SubjectBreakdownItem[]>([])

const rangeOptions = [
  { value: 'TODAY', label: '今日' },
  { value: 'WEEK', label: '本周' },
  { value: 'MONTH', label: '本月' },
  { value: 'TOTAL', label: '合计' }
]

const colors = [
  '#4fc3f7', '#ff8a65', '#81c784', '#ba68c8',
  '#ffd54f', '#4dd0e1', '#a1887f', '#90a4ae',
  '#e57373', '#7986cb'
]

const indicatorStyle = computed(() => {
  const idx = rangeOptions.findIndex(o => o.value === range.value)
  const w = 100 / rangeOptions.length
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

function formatHours(seconds: number): string {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  return h > 0 ? `${h}h${m}m` : `${m}m`
}

function goBack() {
  router.push('/statistics')
}

async function fetchData() {
  try {
    const res: any = await statisticsApi.subjectBreakdown(range.value)
    breakdown.value = res.data?.subjectBreakdown || []
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => fetchData())
watch(range, () => fetchData())
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
  color: rgba(255,255,255,0.6);
  font-size: 0.9rem;
  cursor: pointer;
  padding: 0.3rem 0;
  margin-bottom: 1.5rem;
  transition: color 0.2s;
}
.back-btn:hover { color: #fff; }

/* Range toggle */
.range-toggle {
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
}
.toggle-track {
  position: relative;
  display: flex;
  background: rgba(255,255,255,0.08);
  border-radius: 20px;
  padding: 3px;
  width: 260px;
}
.toggle-indicator {
  position: absolute;
  top: 3px;
  bottom: 3px;
  background: var(--accent-color);
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
  color: rgba(255,255,255,0.6);
  font-size: 0.85rem;
  cursor: pointer;
  border-radius: 17px;
  transition: color 0.2s;
}
.toggle-btn.active { color: #fff; }
.toggle-btn:hover:not(.active) { color: rgba(255,255,255,0.8); }

/* Chart */
.chart-section {
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
}

/* Detail list */
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
  background: rgba(255,255,255,0.03);
  border-radius: 10px;
  transition: background 0.2s;
}
.detail-row:hover { background: rgba(255,255,255,0.06); }
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

.empty-state {
  text-align: center;
  padding: 3rem 0;
  opacity: 0.4;
  font-size: 0.9rem;
}
</style>
