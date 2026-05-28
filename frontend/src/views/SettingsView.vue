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
            <img v-if="userStore.avatarUrl && !uploading" :src="resolveBackendUrl(userStore.avatarUrl)" alt="avatar" @error="onAvatarError" />
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
          <span v-if="uploadMsg" :class="['upload-msg', uploadMsgType]">{{ uploadMsg }}</span>
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

    <!-- 界面设置入口 -->
    <section class="setting-section">
      <h3>界面</h3>
      <button class="nav-entry-btn" @click="goInterface">
        <span>自定义界面</span>
        <span class="nav-arrow">→</span>
      </button>
    </section>

    <!-- 通知音效 -->
    <section class="setting-section">
      <h3>
        专注结束音效
        <label class="toggle-label">
          <input type="checkbox" v-model="notificationSound" class="toggle-input" />
          <span class="toggle-switch"></span>
        </label>
      </h3>
    </section>

    <!-- 学科管理 -->
    <section class="setting-section">
      <h3>学科管理</h3>
      <button class="nav-entry-btn" @click="goSubjects">
        <span>管理学习学科</span>
        <span class="nav-arrow">→</span>
      </button>
    </section>

    <!-- 快捷工具 -->
    <section class="setting-section">
      <h3>快捷工具</h3>
      <div class="shortcut-manage-card">
        <div class="shortcut-add-form">
          <input v-model="form.name" placeholder="名称" class="shortcut-input" />
          <input v-model="form.url" placeholder="https://..." class="shortcut-input" />
          <div class="shortcut-actions-row">
            <button class="btn-save-sm" @click="saveShortcut">添加</button>
          </div>
          <div v-if="shortcutMsg" :class="['shortcut-msg', shortcutMsgType]">
            <span class="shortcut-msg-icon">{{ shortcutMsgType === 'ok' ? '✓' : '✕' }}</span>
            {{ shortcutMsg }}
          </div>
        </div>
        <div class="shortcut-list-settings">
          <div v-for="s in shortcuts" :key="s.id" class="shortcut-item-settings">
            <span class="shortcut-item-icon">
              <img v-if="s.icon && s.icon.startsWith('http')" :src="s.icon" alt="" class="shortcut-favicon" @error="onShortcutIconError" />
              <span v-else>{{ s.icon || '🔗' }}</span>
            </span>
            <span class="shortcut-item-name">{{ s.name }}</span>
            <button class="btn-del" @click="deleteShortcut(s.id)">✕</button>
          </div>
          <div v-if="shortcuts.length === 0" class="shortcut-empty">暂无快捷工具</div>
        </div>
      </div>
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

    <!-- 清空数据 -->
    <section class="setting-section">
      <h3>清空数据</h3>
      <button class="danger-btn" @click="showClearConfirm = true">清空全部数据</button>
      <p class="danger-hint">此操作将删除所有专注记录、学科、快捷工具等数据，不可恢复</p>

      <!-- 二次确认弹窗 -->
      <Transition name="fade">
        <div v-if="showClearConfirm" class="clear-confirm-overlay" @click.self="showClearConfirm = false">
          <div class="clear-confirm-card">
            <div class="clear-confirm-icon">⚠️</div>
            <div class="clear-confirm-title">确认清空数据？</div>
            <div class="clear-confirm-desc">所有专注记录、学科、快捷工具、壁纸和设置将被永久删除，此操作不可撤销。</div>
            <div class="clear-confirm-actions">
              <button class="clear-confirm-btn danger" @click="handleClearAll" :disabled="clearing">
                {{ clearing ? '清空中...' : '确认清空' }}
              </button>
              <button class="clear-confirm-btn cancel" @click="showClearConfirm = false">取消</button>
            </div>
          </div>
        </div>
      </Transition>
    </section>

    <!-- 关于 -->
    <section class="setting-section">
      <h3>关于</h3>
      <button class="nav-entry-btn" @click="showAbout = true">
        <span>关于 StudyClock</span>
        <span class="nav-arrow">→</span>
      </button>

      <Transition name="fade">
        <div v-if="showAbout" class="about-overlay" @click.self="showAbout = false">
          <div class="about-card">
            <div class="about-icon">⏱️</div>
            <div class="about-title">StudyClock</div>
            <div class="about-version">v1.0.0</div>
            <div class="about-divider"></div>
            <div class="about-creator">
              <span class="about-creator-label">制作者</span>
              <span class="about-creator-name">初晨</span>
            </div>
            <a
              href="https://github.com/cc0128-lyh"
              target="_blank"
              class="about-github-link"
              @click="openGithub"
            >
              <span class="about-github-icon">🐙</span>
              cc0128-lyh
            </a>
            <button class="about-close-btn" @click="showAbout = false">关闭</button>
          </div>
        </div>
      </Transition>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useSubjectStore } from '@/stores/subject'
import { useTimerStore } from '@/stores/timer'
import { settingsApi } from '@/api/settings'
import { timerApi } from '@/api/timer'
import { shortcutApi, type Shortcut } from '@/api/shortcut'
import { resolveBackendUrl } from '@/api'

const router = useRouter()
const userStore = useUserStore()
const subjectStore = useSubjectStore()
const timerStore = useTimerStore()

const uploading = ref(false)
const fileInput = ref<HTMLInputElement | null>(null)
const uploadMsg = ref('')
const uploadMsgType = ref<'ok' | 'err'>('ok')

function onAvatarError() {
  uploadMsg.value = '头像加载失败，请检查网络连接'
  uploadMsgType.value = 'err'
}

function triggerUpload() {
  fileInput.value?.click()
}

function goSubjects() {
  router.push('/subjects')
}

function goInterface() {
  router.push('/interface')
}

const notificationSound = computed({
  get: () => timerStore.notificationSoundEnabled,
  set: (val: boolean) => timerStore.setNotificationEnabled(val)
})

// 清空数据
const showClearConfirm = ref(false)
const clearing = ref(false)

const showAbout = ref(false)

async function handleClearAll() {
  clearing.value = true
  try {
    await settingsApi.clearAll()
    showClearConfirm.value = false
    alert('所有数据已清空')
  } catch (e: any) {
    alert('清空失败：' + (e.message || '未知错误'))
  } finally {
    clearing.value = false
  }
}

function openGithub(e: MouseEvent) {
  e.preventDefault()
  const url = 'https://github.com/cc0128-lyh'
  if (window.electronAPI) {
    window.electronAPI.launchUrl(url)
  } else {
    window.open(url, '_blank')
  }
}

// shortcut management
const shortcuts = ref<Shortcut[]>([])
const form = ref({ name: '', url: '' })
const importFileInput = ref<HTMLInputElement | null>(null)
const shortcutMsg = ref('')
const shortcutMsgType = ref<'ok' | 'err'>('ok')

async function fetchShortcuts() {
  try {
    const res: any = await shortcutApi.list()
    shortcuts.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

async function saveShortcut() {
  shortcutMsg.value = ''
  if (!form.value.name) {
    shortcutMsg.value = '请输入名称'
    shortcutMsgType.value = 'err'
    return
  }
  if (!form.value.url) {
    shortcutMsg.value = '请输入网址'
    shortcutMsgType.value = 'err'
    return
  }
  try {
    const domain = extractDomain(form.value.url)
    await shortcutApi.create({
      name: form.value.name,
      launchType: 'URL',
      url: form.value.url,
      icon: domain ? `https://${domain}/favicon.ico` : null
    })
    form.value = { name: '', url: '' }
    shortcutMsg.value = '添加成功'
    shortcutMsgType.value = 'ok'
    await fetchShortcuts()
  } catch (e: any) {
    shortcutMsg.value = '添加失败：' + (e.response?.data?.message || e.message || '未知错误')
    shortcutMsgType.value = 'err'
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

async function importShortcut() {
  if (window.electronAPI) {
    const result = await window.electronAPI.pickShortcut()
    if (!result) return
    form.value.name = result.name
    form.value.launchType = 'APP'
    form.value.url = ''
    form.value.appPath = result.appPath || ''
    form.value.icon = result.icon || ''
  } else {
    importFileInput.value?.click()
  }
}

async function onImportFileSelected(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  const name = file.name.replace(/\.(lnk|exe)$/i, '')
  form.value.name = name
  form.value.launchType = 'APP'
  form.value.url = ''
  form.value.appPath = ''
  form.value.icon = ''

  // .lnk: browser can't read shortcut target, use name as hint
  if (file.name.toLowerCase().endsWith('.exe')) {
    form.value.appPath = file.name
  } else {
    form.value.appPath = name + '.lnk'
  }
  input.value = ''
}

function extractDomain(url: string): string | null {
  try {
    const u = new URL(url)
    return u.hostname
  } catch {
    return null
  }
}

function onShortcutIconError(e: Event) {
  const img = e.target as HTMLImageElement
  const fallback = document.createTextNode('🔗')
  img.replaceWith(fallback)
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
    return { backgroundImage: `url(${resolveBackendUrl(userStore.avatarUrl)})`, backgroundSize: 'cover', backgroundPosition: 'center' }
  }
  return { background: '#fff' }
})


async function onFileSelected(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return
  uploading.value = true
  uploadMsg.value = ''
  try {
    const res: any = await settingsApi.uploadAvatar(file)
    if (res.code === 200 && res.data) {
      await userStore.saveAvatar(res.data)
      uploadMsg.value = '头像上传成功'
      uploadMsgType.value = 'ok'
    } else {
      console.warn('avatar upload returned:', res)
      uploadMsg.value = res.message || '上传失败'
      uploadMsgType.value = 'err'
    }
  } catch (e) {
    console.error('avatar upload failed', e)
    uploadMsg.value = '上传出错，请查看控制台'
    uploadMsgType.value = 'err'
  } finally {
    uploading.value = false
    input.value = ''
  }
}

async function saveNickname() {
  if (!userStore.nickname.trim()) return
  await userStore.saveNickname(userStore.nickname.trim())
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
  await userStore.fetchProfile()
  await subjectStore.fetchList()
  await fetchShortcuts()
  timerStore.loadNotificationSetting()
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
  color: var(--text-inverse);
}
.uploading-hint { font-size: 0.75rem; opacity: 0.6; }
.change-avatar-btn {
  background: transparent;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 0.75rem;
  padding: 0.3rem 0.8rem;
}
.change-avatar-btn:hover { color: var(--text-primary); border-color: var(--border-light); }
.upload-msg { font-size: 0.72rem; padding: 0.2rem 0.6rem; border-radius: 8px; max-width: 120px; text-align: center; }
.upload-msg.ok { color: #166534; background: #dcfce7; }
.upload-msg.err { color: #991b1b; background: #fee2e2; }
.nickname-section { flex: 1; }
.nickname-section label { display: block; font-size: 0.8rem; opacity: 0.5; margin-bottom: 0.4rem; }
.nickname-row { display: flex; gap: 0.5rem; }
.nickname-row input {
  flex: 1;
  padding: 0.6rem 1rem;
  background: var(--bg-hover);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  color: var(--text-primary);
  font-size: 1rem;
}
.btn-save-sm {
  padding: 0.6rem 1.2rem;
  background: var(--accent-color);
  border: none;
  border-radius: 10px;
  color: var(--text-inverse);
  cursor: pointer;
  white-space: nowrap;
  font-weight: 500;
}

/* Subject entry */
.nav-entry-btn {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 0.8rem 1rem;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  color: var(--text-secondary);
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}
.nav-entry-btn:hover {
  background: var(--bg-hover);
  border-color: var(--accent-color);
}
.nav-arrow { font-size: 1.1rem; opacity: 0.4; }
.nav-entry-btn:hover .nav-arrow { opacity: 0.8; }

/* Shortcut management */
.shortcut-manage-card {
  background: var(--panel-bg);
  backdrop-filter: blur(10px);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 1rem;
}
.shortcut-add-form {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
  align-items: center;
}
.shortcut-input {
  flex: 1;
  min-width: 100px;
  padding: 0.5rem 0.7rem;
  background: var(--bg-hover);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-primary);
  font-size: 0.85rem;
  outline: none;
}
.shortcut-input:focus { border-color: var(--accent-color); }
.shortcut-input option { background: var(--bg-primary); color: var(--text-primary); }
.shortcut-actions-row {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}
.btn-import {
  padding: 0.6rem 1rem;
  background: transparent;
  border: 1px solid var(--border-color);
  border-radius: 10px;
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 0.85rem;
  white-space: nowrap;
}
.btn-import:hover { color: var(--text-primary); border-color: var(--border-light); }
.shortcut-list-settings {
  border-top: 1px solid var(--border-color);
  padding-top: 0.5rem;
}
.shortcut-item-settings {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.5rem 0.3rem;
  border-bottom: 1px solid var(--border-color);
}
.shortcut-item-settings:last-child { border-bottom: none; }
.shortcut-item-icon { font-size: 1.1rem; flex-shrink: 0; display: flex; align-items: center; }
.shortcut-item-name { flex: 1; font-size: 0.9rem; }
.shortcut-favicon { width: 20px; height: 20px; border-radius: 3px; object-fit: contain; }
.btn-del {
  width: 26px;
  height: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  border-radius: 50%;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 0.75rem;
  transition: all 0.2s;
  flex-shrink: 0;
}
.btn-del:hover {
  background: rgba(239, 68, 68, 0.12);
  color: #ef4444;
}
.shortcut-empty { text-align: center; font-size: 0.8rem; opacity: 0.4; padding: 0.8rem 0; }
.shortcut-msg {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  margin-top: 0.5rem;
  padding: 0.35rem 0.9rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  animation: msgFadeIn 0.25s ease-out;
}
.shortcut-msg.ok {
  color: #166534;
  background: #dcfce7;
}
.shortcut-msg.err {
  color: #991b1b;
  background: #fee2e2;
}
.shortcut-msg-icon { font-size: 0.75rem; font-weight: 700; }
@keyframes msgFadeIn {
  from { opacity: 0; transform: translateY(-4px); }
  to   { opacity: 1; transform: translateY(0); }
}

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
  background: var(--bg-hover);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-primary);
  font-size: 0.85rem;
  outline: none;
  color-scheme: var(--color-scheme);
}
.debug-input:focus { border-color: var(--accent-color); }
.debug-select { min-width: 100px; cursor: pointer; }
.debug-select option { background: var(--bg-primary); color: var(--text-primary); }
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
  background: var(--toggle-bg);
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
  background: var(--text-primary);
  top: 2px;
  left: 2px;
  transition: transform 0.2s;
}
.toggle-input:checked + .toggle-switch { background: var(--toggle-active-bg); }
.toggle-input:checked + .toggle-switch::after { transform: translateX(16px); }

/* Clear data */
.danger-btn {
  padding: 0.6rem 1.2rem;
  background: transparent;
  border: 1px solid #ef4444;
  border-radius: 10px;
  color: #ef4444;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
}
.danger-btn:hover {
  background: #ef4444;
  color: #fff;
}
.danger-hint {
  font-size: 0.75rem;
  opacity: 0.4;
  margin-top: 0.4rem;
}

/* Clear confirm overlay */
.clear-confirm-overlay {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.5);
  z-index: 100;
}
.clear-confirm-card {
  background: var(--panel-bg);
  backdrop-filter: blur(20px);
  border: 1px solid var(--border-color);
  border-radius: 24px;
  padding: 2rem 2.2rem;
  text-align: center;
  min-width: 300px;
  max-width: 400px;
  width: 90%;
  animation: glassIn 0.3s ease;
}
.clear-confirm-icon {
  font-size: 2.5rem;
  margin-bottom: 0.8rem;
}
.clear-confirm-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.8rem;
}
.clear-confirm-desc {
  font-size: 0.85rem;
  opacity: 0.6;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}
.clear-confirm-actions {
  display: flex;
  gap: 0.8rem;
  justify-content: center;
}
.clear-confirm-btn {
  padding: 0.6rem 1.8rem;
  border: none;
  border-radius: 20px;
  font-size: 0.92rem;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}
.clear-confirm-btn.danger {
  background: #ef4444;
  color: #fff;
}
.clear-confirm-btn.danger:hover {
  opacity: 0.85;
}
.clear-confirm-btn.danger:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.clear-confirm-btn.cancel {
  background: transparent;
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}
.clear-confirm-btn.cancel:hover {
  color: var(--text-primary);
  border-color: var(--text-secondary);
}

@keyframes glassIn {
  from { transform: translateY(20px) scale(0.97); opacity: 0; }
  to { transform: translateY(0) scale(1); opacity: 1; }
}

/* About overlay */
.about-overlay {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.5);
  z-index: 100;
}
.about-card {
  background: var(--panel-bg);
  backdrop-filter: blur(20px);
  border: 1px solid var(--border-color);
  border-radius: 24px;
  padding: 2.5rem 2.5rem;
  text-align: center;
  min-width: 280px;
  max-width: 360px;
  width: 90%;
  animation: glassIn 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}
.about-icon { font-size: 3rem; margin-bottom: 0.3rem; }
.about-title { font-size: 1.3rem; font-weight: 600; }
.about-version { font-size: 0.8rem; opacity: 0.4; }
.about-divider { width: 40px; height: 2px; background: var(--border-color); margin: 0.6rem 0; border-radius: 1px; }
.about-creator { display: flex; gap: 0.4rem; align-items: center; margin-bottom: 0.3rem; }
.about-creator-label { font-size: 0.85rem; opacity: 0.5; }
.about-creator-name { font-size: 1rem; font-weight: 500; }
.about-github-link {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.5rem 1.2rem;
  background: var(--bg-hover);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  color: var(--text-primary);
  text-decoration: none;
  font-size: 0.85rem;
  margin: 0.5rem 0 1rem;
  transition: all 0.2s;
}
.about-github-link:hover {
  background: var(--accent-muted);
  border-color: var(--accent-color);
}
.about-github-icon { font-size: 1.1rem; }
.about-close-btn {
  padding: 0.5rem 2rem;
  background: transparent;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.2s;
}
.about-close-btn:hover {
  color: var(--text-primary);
  border-color: var(--text-secondary);
}
</style>
