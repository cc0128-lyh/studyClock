<template>
  <div class="settings-view">
    <h2>设置</h2>

    <!-- 用户资料 -->
    <section class="setting-section">
      <h3>用户资料</h3>
      <div class="profile-card">
        <div class="avatar-section">
          <div class="avatar-preview" :style="avatarStyle" @click="triggerUpload">
            <span v-if="!userStore.avatarUrl && !uploading" class="avatar-placeholder">
              {{ (userStore.nickname || '?').charAt(0) }}
            </span>
            <img v-if="userStore.avatarUrl && !uploading" :src="userStore.avatarUrl" alt="avatar" />
            <span v-if="uploading" class="uploading-hint">上传中...</span>
          </div>
          <input
            ref="fileInput"
            type="file"
            accept="image/*"
            style="display:none"
            @change="onFileSelected"
          />
          <button class="change-avatar-btn" @click="triggerUpload">更换头像</button>
        </div>
        <div class="nickname-section">
          <label>昵称</label>
          <div class="nickname-row">
            <input v-model="userStore.nickname" placeholder="输入你的昵称" maxlength="20" />
            <button class="btn-save-sm" @click="saveNickname">保存</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 壁纸 -->
    <section class="setting-section">
      <h3>壁纸</h3>
      <div class="wallpaper-list">
        <div
          v-for="wp in wallpaperStore.list"
          :key="wp.id"
          :class="['wallpaper-item', { active: wallpaperStore.activeWallpaper?.id === wp.id }]"
          @click="wallpaperStore.setActive(wp.id)"
        >
          <div
            class="wallpaper-preview"
            :style="wp.type === 'COLOR' ? { background: wp.bgColor || '#333' } : {}"
          >
            <img v-if="wp.type === 'IMAGE' && wp.filePath" :src="wp.filePath" :alt="wp.name" />
          </div>
          <span class="wallpaper-name">{{ wp.name }}</span>
        </div>
      </div>
    </section>

    <!-- 默认专注时长 -->
    <section class="setting-section">
      <h3>默认专注时长</h3>
      <div class="duration-setting">
        <button
          v-for="m in [5, 15, 25, 45, 60]"
          :key="m"
          :class="['dur-btn', { active: defaultMinutes === m }]"
          @click="setDefaultDuration(m)"
        >
          {{ m }} 分钟
        </button>
      </div>
    </section>

    <!-- 学科管理 -->
    <section class="setting-section">
      <h3>学科管理</h3>
      <button class="nav-entry-btn" @click="goSubjects">
        <span>管理学习学科</span>
        <span class="nav-arrow">→</span>
      </button>
    </section>

    <!-- 调试模式 -->
    <section class="setting-section">
      <h3>
        调试模式
        <label class="toggle-label">
          <input type="checkbox" v-model="debugMode" class="toggle-input" />
          <span class="toggle-switch"></span>
        </label>
      </h3>
      <div v-if="debugMode" class="debug-panel">
        <p class="debug-hint">手动添加历史专注记录（不可添加未来时间）</p>
        <div class="debug-row">
          <div class="debug-field">
            <label>开始时间</label>
            <input type="datetime-local" v-model="debugStart" class="debug-input" />
          </div>
          <div class="debug-field">
            <label>结束时间</label>
            <input type="datetime-local" v-model="debugEnd" class="debug-input" />
          </div>
          <div class="debug-field">
            <label>学科</label>
            <select v-model="debugSubject" class="debug-input debug-select">
              <option value="">无学科</option>
              <option v-for="s in subjectStore.list" :key="s.id" :value="s.name">{{ s.name }}</option>
            </select>
          </div>
          <button class="btn-save-sm debug-add-btn" @click="addDebugSession" :disabled="debugAdding">
            {{ debugAdding ? '添加中...' : '添加' }}
          </button>
        </div>
        <div v-if="debugMsg" :class="['debug-msg', debugMsgType]">{{ debugMsg }}</div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useWallpaperStore } from '@/stores/wallpaper'
import { useUserStore } from '@/stores/user'
import { useSubjectStore } from '@/stores/subject'
import { settingsApi } from '@/api/settings'
import { timerApi } from '@/api/timer'

const router = useRouter()
const wallpaperStore = useWallpaperStore()
const userStore = useUserStore()
const subjectStore = useSubjectStore()
const defaultMinutes = ref(25)

const uploading = ref(false)
const fileInput = ref<HTMLInputElement | null>(null)

function goSubjects() {
  router.push('/subjects')
}

// debug mode
const debugMode = ref(false)
const debugStart = ref('')
const debugEnd = ref('')
const debugSubject = ref('')
const debugAdding = ref(false)
const debugMsg = ref('')
const debugMsgType = ref<'ok' | 'err'>('ok')

function toLocalDatetimeString(date: Date): string {
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`
}

function setDefaultTimeRange() {
  const now = new Date()
  const end = new Date(now)
  const start = new Date(now.getTime() - 90 * 60 * 1000) // 90 min ago
  debugEnd.value = toLocalDatetimeString(end)
  debugStart.value = toLocalDatetimeString(start)
}

// set defaults when debug mode is enabled
watch(debugMode, (on) => {
  if (on) setDefaultTimeRange()
})

const avatarStyle = computed(() => {
  if (userStore.avatarUrl) {
    return { backgroundImage: `url(${userStore.avatarUrl})`, backgroundSize: 'cover', backgroundPosition: 'center' }
  }
  return { background: 'var(--accent-color)' }
})

function triggerUpload() {
  fileInput.value?.click()
}

async function onFileSelected(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  uploading.value = true
  try {
    const res: any = await settingsApi.uploadAvatar(file)
    if (res.code === 200 && res.data) {
      await userStore.saveAvatar(res.data)
    } else {
      console.warn('avatar upload returned:', res)
      alert(res.message || '上传失败')
    }
  } catch (e) {
    console.error('avatar upload failed', e)
    alert('上传出错，请查看控制台')
  } finally {
    uploading.value = false
    input.value = ''
  }
}

async function saveNickname() {
  if (!userStore.nickname.trim()) return
  await userStore.saveNickname(userStore.nickname.trim())
}

async function setDefaultDuration(m: number) {
  defaultMinutes.value = m
  await settingsApi.set('defaultFocusMinutes', String(m))
}

async function addDebugSession() {
  debugMsg.value = ''
  if (!debugStart.value || !debugEnd.value) {
    debugMsg.value = '请填写开始和结束时间'
    debugMsgType.value = 'err'
    return
  }
  const start = new Date(debugStart.value)
  const end = new Date(debugEnd.value)
  const now = new Date()

  if (end > now) {
    debugMsg.value = '结束时间不能是未来时间'
    debugMsgType.value = 'err'
    return
  }
  if (start >= end) {
    debugMsg.value = '结束时间必须晚于开始时间'
    debugMsgType.value = 'err'
    return
  }

  debugAdding.value = true
  try {
    await timerApi.addDebug(
      debugStart.value.replace('T', 'T') + ':00',
      debugEnd.value.replace('T', 'T') + ':00',
      debugSubject.value || undefined
    )
    debugMsg.value = '添加成功！'
    debugMsgType.value = 'ok'
    setDefaultTimeRange()
  } catch (e: any) {
    debugMsg.value = '添加失败：' + (e.response?.data?.message || e.message || '未知错误')
    debugMsgType.value = 'err'
  } finally {
    debugAdding.value = false
  }
}

onMounted(async () => {
  await wallpaperStore.fetchList()
  await userStore.fetchProfile()
  await subjectStore.fetchList()
  try {
    const res: any = await settingsApi.get('defaultFocusMinutes')
    if (res.data) defaultMinutes.value = parseInt(res.data)
  } catch (_) {}
})
</script>

<style scoped>
.settings-view {
  width: 100%;
  max-width: 700px;
  margin: 0 auto;
  padding: 1.5rem 2rem;
  align-self: stretch;
  overflow-y: auto;
}
h2 { font-weight: 400; letter-spacing: 0.1em; margin-bottom: 1.5rem; text-align: center; }
.setting-section { margin-bottom: 1.8rem; }
.setting-section h3 { font-weight: 400; font-size: 1rem; opacity: 0.7; margin-bottom: 0.8rem; }

/* User Profile */
.profile-card {
  background: var(--panel-bg);
  backdrop-filter: blur(10px);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 1.5rem;
  display: flex;
  gap: 2rem;
  align-items: center;
}
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}
.avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border: 2px solid var(--border-color);
  transition: border-color 0.2s;
}
.avatar-preview:hover { border-color: var(--accent-color); }
.avatar-preview img { width: 100%; height: 100%; object-fit: cover; }
.avatar-placeholder {
  font-size: 2rem;
  font-weight: 600;
  color: #fff;
}
.uploading-hint { font-size: 0.75rem; opacity: 0.6; }
.change-avatar-btn {
  background: transparent;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  color: rgba(255,255,255,0.7);
  cursor: pointer;
  font-size: 0.75rem;
  padding: 0.3rem 0.8rem;
}
.change-avatar-btn:hover { color: #fff; border-color: rgba(255,255,255,0.3); }
.nickname-section { flex: 1; }
.nickname-section label { display: block; font-size: 0.8rem; opacity: 0.5; margin-bottom: 0.4rem; }
.nickname-row { display: flex; gap: 0.5rem; }
.nickname-row input {
  flex: 1;
  padding: 0.6rem 1rem;
  background: rgba(255,255,255,0.05);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  color: #fff;
  font-size: 1rem;
}
.btn-save-sm {
  padding: 0.6rem 1.2rem;
  background: var(--accent-color);
  border: none;
  border-radius: 10px;
  color: #fff;
  cursor: pointer;
  white-space: nowrap;
}

/* Wallpaper */
.wallpaper-list { display: flex; gap: 1rem; flex-wrap: wrap; }
.wallpaper-item {
  width: 120px;
  cursor: pointer;
  border-radius: 12px;
  border: 2px solid transparent;
  overflow: hidden;
  transition: all 0.2s;
  text-align: center;
}
.wallpaper-item.active { border-color: var(--accent-color); }
.wallpaper-item:hover { opacity: 0.85; }
.wallpaper-preview {
  width: 120px;
  height: 72px;
  border-radius: 8px;
  overflow: hidden;
  background: #333;
}
.wallpaper-preview img { width: 100%; height: 100%; object-fit: cover; }
.wallpaper-name { display: block; margin-top: 0.3rem; font-size: 0.8rem; opacity: 0.7; }

/* Duration */
.duration-setting { display: flex; gap: 0.5rem; flex-wrap: wrap; }
.dur-btn {
  padding: 0.5rem 1.2rem;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  background: transparent;
  color: #fff;
  cursor: pointer;
  transition: all 0.2s;
}
.dur-btn:hover { background: rgba(255,255,255,0.1); }
.dur-btn.active { background: var(--accent-color); border-color: var(--accent-color); }

/* Subject entry */
.nav-entry-btn {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 0.8rem 1rem;
  background: rgba(255,255,255,0.03);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  color: rgba(255,255,255,0.8);
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}
.nav-entry-btn:hover {
  background: rgba(255,255,255,0.06);
  border-color: var(--accent-color);
}
.nav-arrow { font-size: 1.1rem; opacity: 0.4; }
.nav-entry-btn:hover .nav-arrow { opacity: 0.8; }

/* Debug mode */
.debug-panel {
  background: var(--panel-bg);
  backdrop-filter: blur(10px);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 1.2rem;
}
.debug-hint { font-size: 0.8rem; opacity: 0.5; margin-bottom: 1rem; }
.debug-row { display: flex; gap: 0.8rem; align-items: flex-end; flex-wrap: wrap; }
.debug-field { display: flex; flex-direction: column; gap: 0.3rem; }
.debug-field label { font-size: 0.75rem; opacity: 0.5; }
.debug-input {
  padding: 0.5rem 0.7rem;
  background: rgba(255,255,255,0.05);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: #fff;
  font-size: 0.85rem;
  outline: none;
  color-scheme: dark;
}
.debug-input:focus { border-color: var(--accent-color); }
.debug-select { min-width: 100px; cursor: pointer; }
.debug-select option { background: #1a1a2e; color: #fff; }
.debug-add-btn { height: 36px; }
.debug-msg { margin-top: 0.8rem; font-size: 0.85rem; }
.debug-msg.ok { color: #4ade80; }
.debug-msg.err { color: #f87171; }

.toggle-label {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  margin-left: 0.5rem;
  vertical-align: middle;
}
.toggle-input { display: none; }
.toggle-switch {
  width: 36px;
  height: 20px;
  background: rgba(255,255,255,0.15);
  border-radius: 10px;
  position: relative;
  transition: background 0.2s;
}
.toggle-switch::after {
  content: '';
  position: absolute;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #fff;
  top: 2px;
  left: 2px;
  transition: transform 0.2s;
}
.toggle-input:checked + .toggle-switch { background: var(--accent-color); }
.toggle-input:checked + .toggle-switch::after { transform: translateX(16px); }
</style>
