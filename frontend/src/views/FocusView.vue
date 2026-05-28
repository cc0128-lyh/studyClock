<template>
  <div class="focus-view">
    <!-- top progress bar -->
    <div v-if="timerStore.currentSession" class="top-progress-bar">
      <div class="top-progress-subject">{{ timerStore.selectedSubject || '其他' }}</div>
      <div class="top-progress-track">
        <div class="top-progress-fill" :style="{ width: `${timerStore.progress * 100}%` }"></div>
      </div>
    </div>

    <div class="center-content">
      <ClockDisplay
        label="专注时间"
        :display-time="timerStore.formattedTime"
      />
      <TimerControls />
    </div>

    <ShortcutPanel
      :shortcuts="shortcuts"
      @manage="showManage = true"
    />

    <BreakReminder
      :visible="timerStore.justCompleted"
      :minutes="timerStore.completedMinutes"
      :seconds="timerStore.completedSeconds"
      @close="timerStore.acknowledgeCompletion()"
      @start-next="startNextRound"
    />

    <div v-if="showManage" class="modal-overlay" @click.self="showManage = false">
      <div class="modal">
        <h3>管理快捷工具</h3>
        <div class="shortcut-form">
          <input v-model="form.name" placeholder="名称" />
          <select v-model="form.launchType">
            <option value="URL">网址</option>
            <option value="APP">应用</option>
          </select>
          <input v-if="form.launchType === 'URL'" v-model="form.url" placeholder="https://..." />
          <input v-else v-model="form.appPath" placeholder="应用路径" />
          <button class="btn-save" @click="saveShortcut">添加</button>
        </div>
        <div class="shortcut-list-modal">
          <div v-for="s in shortcuts" :key="s.id" class="shortcut-item">
            <span>{{ s.name }}</span>
            <button class="btn-del" @click="deleteShortcut(s.id)">✕</button>
          </div>
        </div>
        <button class="btn-close" @click="showManage = false">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import ClockDisplay from '@/components/ClockDisplay.vue'
import TimerControls from '@/components/TimerControls.vue'
import ShortcutPanel from '@/components/ShortcutPanel.vue'
import BreakReminder from '@/components/BreakReminder.vue'
import { useTimerStore } from '@/stores/timer'
import { shortcutApi, type Shortcut } from '@/api/shortcut'

const timerStore = useTimerStore()
const shortcuts = ref<Shortcut[]>([])
const showManage = ref(false)

const form = ref({ name: '', launchType: 'URL', url: '', appPath: '' })

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

async function saveShortcut() {
  if (!form.value.name) return
  try {
    await shortcutApi.create(form.value as any)
    form.value = { name: '', launchType: 'URL', url: '', appPath: '' }
    await fetchShortcuts()
  } catch (e) {
    console.error(e)
  }
}

async function deleteShortcut(id: number) {
  try {
    await shortcutApi.delete(id)
    await fetchShortcuts()
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchShortcuts()
  timerStore.loadDefaultTarget()
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
}
.center-content { text-align: center; }

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
  background: rgba(255,255,255,0.1);
}
.top-progress-fill {
  height: 100%;
  background: var(--accent-color);
  transition: width 1s linear;
}

.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(0,0,0,0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
}
.modal {
  background: var(--secondary-color);
  border-radius: 16px;
  padding: 2rem;
  min-width: 400px;
  border: 1px solid var(--border-color);
}
.modal h3 { margin-bottom: 1rem; font-weight: 500; }
.shortcut-form { display: flex; flex-wrap: wrap; gap: 0.5rem; margin-bottom: 1rem; }
.shortcut-form input, .shortcut-form select {
  flex: 1;
  min-width: 120px;
  padding: 0.5rem 0.8rem;
  background: rgba(255,255,255,0.05);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: #fff;
  font-size: 0.85rem;
}
.btn-save {
  padding: 0.5rem 1.2rem;
  background: #fff;
  border: none;
  border-radius: 8px;
  color: #0a0a0a;
  cursor: pointer;
  font-weight: 500;
}
.shortcut-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid var(--border-color);
}
.btn-del { background: none; border: none; color: rgba(255,255,255,0.4); cursor: pointer; }
.btn-del:hover { color: var(--accent-color); }
.btn-close {
  width: 100%;
  margin-top: 1rem;
  padding: 0.5rem;
  background: rgba(255,255,255,0.05);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: #fff;
  cursor: pointer;
}
</style>
