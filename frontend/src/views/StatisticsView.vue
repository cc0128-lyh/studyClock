<template>
  <div class="statistics-view">
    <h2>专注统计</h2>

    <!-- 今日学习 — 重点展示 -->
    <div v-if="summary.subjectBreakdown && summary.subjectBreakdown.length > 0" class="today-section">
      <div class="today-header">
        <h3>今日学习</h3>
        <span class="today-subtitle">{{ formatHours(summary.dailySeconds) }} · {{ summary.subjectBreakdown.length }} 个学科</span>
      </div>
      <div class="today-grid">
        <div v-for="item in summary.subjectBreakdown" :key="item.subjectName" class="today-card">
          <div class="tc-name">{{ item.subjectName }}</div>
          <div class="tc-time">{{ formatHours(item.totalSeconds) }}</div>
          <div class="tc-count">{{ item.sessionCount }} 次</div>
        </div>
      </div>
    </div>
    <div v-else class="today-section today-empty">
      <h3>今日学习</h3>
      <p class="empty-hint">今天还没有专注记录</p>
    </div>

    <!-- 专注统计概览 -->
    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-value">{{ formatHours(summary.dailySeconds) }}</div>
        <div class="stat-label">今日</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ formatHours(summary.weeklySeconds) }}</div>
        <div class="stat-label">本周</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ formatHours(summary.monthlySeconds) }}</div>
        <div class="stat-label">本月</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ formatHours(summary.totalSeconds) }}</div>
        <div class="stat-label">总计</div>
      </div>
    </div>
    <div class="session-count">
      完成专注次数: <strong>{{ summary.sessionCount }}</strong>
    </div>

    <button class="detail-btn" @click="goDetail">详细专注信息 →</button>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { statisticsApi, type FocusSummary } from '@/api/statistics'

const router = useRouter()
const summary = ref<FocusSummary>({
  totalSeconds: 0, sessionCount: 0,
  dailySessionCount: 0, weeklySessionCount: 0, monthlySessionCount: 0,
  dailySeconds: 0, weeklySeconds: 0, monthlySeconds: 0,
  subjectBreakdown: []
})

function formatHours(seconds: number): string {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  return h > 0 ? `${h}h${m}m` : `${m}m`
}

function goDetail() {
  router.push('/statistics/detail')
}

onMounted(async () => {
  try {
    const res: any = await statisticsApi.summary()
    summary.value = res.data
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
.statistics-view {
  text-align: center;
  padding: 2rem;
}
h2 { font-weight: 400; letter-spacing: 0.1em; margin-bottom: 2rem; }

/* 今日学习 — 重点展示 */
.today-section {
  margin-bottom: 2.5rem;
}
.today-header {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 0.8rem;
  margin-bottom: 1.2rem;
}
.today-header h3 {
  font-weight: 500;
  font-size: 1.3rem;
  margin: 0;
}
.today-subtitle {
  font-size: 0.85rem;
  opacity: 0.5;
}
.today-grid {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}
.today-card {
  background: var(--panel-bg);
  border: 1px solid var(--border-color);
  border-radius: 18px;
  padding: 1.5rem 2.2rem;
  min-width: 140px;
  text-align: center;
  transition: all 0.2s;
}
.today-card:hover {
  border-color: var(--accent-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.4);
}
.tc-name {
  font-size: 1rem;
  font-weight: 500;
  margin-bottom: 0.5rem;
}
.tc-time {
  font-size: 2rem;
  font-weight: 300;
  font-family: var(--font-mono);
  color: var(--text-primary);
}
.tc-count {
  font-size: 0.8rem;
  opacity: 0.45;
  margin-top: 0.3rem;
}
.today-empty h3 {
  font-weight: 400;
  font-size: 1rem;
  opacity: 0.5;
  margin-bottom: 0.5rem;
}
.empty-hint { font-size: 0.85rem; opacity: 0.35; }

/* 统计概览 */
.stat-grid { display: flex; gap: 1.5rem; justify-content: center; flex-wrap: wrap; }
.stat-card {
  background: var(--panel-bg);
  backdrop-filter: blur(10px);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 1.5rem 2.2rem;
  min-width: 130px;
}
.stat-value { font-size: 1.6rem; font-weight: 300; font-family: var(--font-mono); }
.stat-label { margin-top: 0.4rem; font-size: 0.8rem; opacity: 0.55; }
.session-count { margin-top: 1.5rem; font-size: 0.85rem; opacity: 0.6; }
.detail-btn {
  margin-top: 1.5rem;
  padding: 0.7rem 2rem;
  background: transparent;
  border: 1px solid var(--border-subtle);
  border-radius: 20px;
  color: var(--text-secondary);
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}
.detail-btn:hover {
  background: var(--bg-hover);
  border-color: var(--text-secondary);
  color: var(--text-primary);
}
</style>
