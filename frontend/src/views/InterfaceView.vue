<template>
  <div class="interface-view">
    <button class="back-btn" @click="goBack">← 返回</button>
    <h2>自定义界面</h2>

    <!-- 主题选择 -->
    <section class="setting-section">
      <h3>主题模式</h3>
      <div class="theme-options">
        <div
          :class="['theme-card', { active: themeStore.themeMode === 'dark' }]"
          @click="themeStore.setTheme('dark')"
        >
          <div class="theme-preview theme-dark">
            <div class="tp-bar"></div>
            <div class="tp-body">
              <div class="tp-dot"></div>
              <div class="tp-line"></div>
            </div>
          </div>
          <div class="theme-label">
            <span class="theme-name">黑夜模式</span>
            <span v-if="themeStore.themeMode === 'dark'" class="theme-check">✓</span>
          </div>
        </div>
        <div
          :class="['theme-card', { active: themeStore.themeMode === 'light' }]"
          @click="themeStore.setTheme('light')"
        >
          <div class="theme-preview theme-light">
            <div class="tp-bar"></div>
            <div class="tp-body">
              <div class="tp-dot"></div>
              <div class="tp-line"></div>
            </div>
          </div>
          <div class="theme-label">
            <span class="theme-name">日间模式</span>
            <span v-if="themeStore.themeMode === 'light'" class="theme-check">✓</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 专注时间样式 -->
    <section class="setting-section">
      <h3>专注时间样式</h3>

      <!-- 位置预设 -->
      <div class="pos-label">计时模式</div>
      <div class="pos-options">
        <div
          :class="['pos-card', { active: focusStyle.style.timerMode === 'countdown' }]"
          @click="focusStyle.setTimerMode('countdown')"
        >
          <div class="pos-preview" style="height:60px">
            <div class="pp-clock-sm">25:00</div>
            <div class="pp-controls"><span></span><span></span></div>
          </div>
          <span class="pos-name">倒计时</span>
        </div>
        <div
          :class="['pos-card', { active: focusStyle.style.timerMode === 'countup' }]"
          @click="focusStyle.setTimerMode('countup')"
        >
          <div class="pos-preview" style="height:60px">
            <div class="pp-clock-sm">00:00 →</div>
            <div class="pp-controls"><span></span><span></span></div>
          </div>
          <span class="pos-name">正计时</span>
        </div>
      </div>

      <div class="pos-label">位置样式</div>
      <div class="pos-options">
        <div
          :class="['pos-card', { active: focusStyle.style.position === 'center' }]"
          @click="focusStyle.setPosition('center')"
        >
          <div class="pos-preview pos-preview-center">
            <div class="pp-clock">12:34</div>
            <div class="pp-controls"><span></span><span></span></div>
          </div>
          <span class="pos-name">经典居中</span>
        </div>
        <div
          :class="['pos-card', { active: focusStyle.style.position === 'top' }]"
          @click="focusStyle.setPosition('top')"
        >
          <div class="pos-preview pos-preview-top">
            <div class="pp-clock-sm">12:34</div>
            <div class="pp-controls"><span></span><span></span></div>
          </div>
          <span class="pos-name">顶部精简</span>
        </div>
        <div
          :class="['pos-card', { active: focusStyle.style.position === 'side' }]"
          @click="focusStyle.setPosition('side')"
        >
          <div class="pos-preview pos-preview-side">
            <div class="pp-row">
              <div class="pp-clock-sm">12:34</div>
              <div class="pp-controls pp-controls-stack"><span></span><span></span></div>
            </div>
          </div>
          <span class="pos-name">侧边显示</span>
        </div>
      </div>

      <!-- 字体 -->
      <div class="style-row">
        <label>字体</label>
        <select v-model="selectedFont" class="style-select">
          <option value="">系统默认</option>
          <option value="monospace, var(--font-mono)">等宽字体</option>
          <option value="KaiTi, serif">楷体</option>
          <option value="SimHei, sans-serif">黑体</option>
          <option value="SimSun, serif">宋体</option>
          <option value="'PingFang SC', 'Microsoft YaHei', sans-serif">苹方</option>
          <option value="'Noto Sans SC', sans-serif">Noto Sans</option>
          <option value="serif">衬线字体</option>
          <option value="sans-serif">无衬线字体</option>
        </select>
      </div>

      <!-- 字体大小 -->
      <div class="style-row">
        <label>字体大小</label>
        <div class="size-control">
          <input
            type="range"
            min="1.5"
            max="16"
            step="0.5"
            v-model.number="fontSizeInput"
            class="size-slider"
          />
          <span class="size-value">{{ fontSizeInput }}rem</span>
          <button class="btn-reset" @click="fontSizeInput = 0" title="恢复默认">↺</button>
        </div>
      </div>

      <!-- 字体配色 -->
      <div class="style-row">
        <label>字体配色</label>
        <div class="color-area">
          <div class="color-presets">
            <div
              v-for="c in colorPresets"
              :key="c"
              :class="['color-dot', { active: focusStyle.style.color === c, 'is-transparent': !c }]"
              :style="c ? { background: c } : {}"
              @click="focusStyle.setColor(c)"
              :title="c || '恢复默认'"
            ></div>
          </div>
          <input
            type="color"
            v-model="customColor"
            class="color-picker"
            title="自定义颜色"
          />
        </div>
      </div>
    </section>

    <!-- 壁纸 -->
    <section class="setting-section">
      <h3>壁纸</h3>

      <!-- 上传区域 -->
      <div class="upload-area">
        <input
          ref="fileInput"
          type="file"
          accept="image/*"
          style="display:none"
          @change="onFileSelected"
        />
        <button class="upload-btn" @click="fileInput?.click()" :disabled="uploading">
          {{ uploading ? '上传中...' : '+ 上传壁纸' }}
        </button>
        <span v-if="uploadMsg" :class="['upload-msg', uploadMsgType]">{{ uploadMsg }}</span>
      </div>

      <!-- 壁纸列表 -->
      <div class="wallpaper-list">
        <div
          v-for="wp in wallpaperStore.list"
          :key="wp.id"
          :class="['wallpaper-item', { active: wallpaperStore.activeWallpaper?.id === wp.id }]"
        >
          <div class="wallpaper-preview-wrap" @click="wallpaperStore.setActive(wp.id)">
            <div
              class="wallpaper-preview"
              :style="wp.type === 'COLOR' ? { background: wp.bgColor || '#333' } : {}"
            >
              <img v-if="wp.type === 'IMAGE' && wp.filePath" :src="resolveBackendUrl(wp.filePath)" :alt="wp.name" />
            </div>
            <span class="wallpaper-name">{{ wp.name }}</span>
          </div>
          <button class="btn-del-wallpaper" @click="deleteWallpaper(wp.id)" title="删除">✕</button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { useWallpaperStore } from '@/stores/wallpaper'
import { useFocusStyleStore } from '@/stores/focusStyle'
import { wallpaperApi } from '@/api/wallpaper'
import { resolveBackendUrl } from '@/api'

const router = useRouter()
const themeStore = useThemeStore()
const wallpaperStore = useWallpaperStore()
const focusStyle = useFocusStyleStore()

const fileInput = ref<HTMLInputElement | null>(null)
const uploading = ref(false)
const uploadMsg = ref('')
const uploadMsgType = ref<'ok' | 'err'>('ok')

// Font selection — sync with store
const selectedFont = computed({
  get: () => focusStyle.style.fontFamily,
  set: (v: string) => focusStyle.setFontFamily(v)
})

// Font size — 0 = use position default
const fontSizeInput = computed({
  get: () => focusStyle.style.fontSize,
  set: (v: number) => focusStyle.setFontSize(v)
})

// Color presets + custom color picker
const colorPresets = ['', '#ffffff', '#94a3b8', '#f87171', '#fb923c', '#fbbf24', '#a3e635', '#4ade80', '#2dd4bf', '#60a5fa', '#818cf8', '#c084fc', '#f472b6']

const customColor = computed({
  get: () => {
    // if current color is one of the presets (except empty), show white as default
    if (!focusStyle.style.color || colorPresets.includes(focusStyle.style.color)) return '#ffffff'
    return focusStyle.style.color
  },
  set: (v: string) => focusStyle.setColor(v)
})

function goBack() {
  router.push('/settings')
}

async function onFileSelected(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  uploading.value = true
  uploadMsg.value = ''
  try {
    await wallpaperStore.upload(file)
    uploadMsg.value = '上传成功'
    uploadMsgType.value = 'ok'
  } catch (e: any) {
    uploadMsg.value = '上传失败：' + (e.response?.data?.message || e.message || '未知错误')
    uploadMsgType.value = 'err'
  } finally {
    uploading.value = false
    input.value = ''
  }
}

async function deleteWallpaper(id: number) {
  try {
    await wallpaperApi.delete(id)
    await wallpaperStore.fetchList()
    await wallpaperStore.fetchActive()
  } catch (e) {
    console.error(e)
  }
}

onMounted(async () => {
  await wallpaperStore.fetchList()
})
</script>

<style scoped>
.interface-view {
  width: 100%;
  max-width: 700px;
  margin: 0 auto;
  padding: 1.5rem 2rem;
  align-self: stretch;
  overflow-y: auto;
}
.back-btn {
  background: none;
  border: none;
  color: var(--text-secondary);
  font-size: 0.9rem;
  cursor: pointer;
  padding: 0.3rem 0;
  margin-bottom: 0.5rem;
  transition: color 0.2s;
}
.back-btn:hover { color: var(--text-primary); }
h2 {
  font-weight: 400;
  letter-spacing: 0.1em;
  margin-bottom: 2rem;
  text-align: center;
}
.setting-section { margin-bottom: 2.5rem; }
.setting-section h3 {
  font-weight: 400;
  font-size: 1rem;
  opacity: 0.7;
  margin-bottom: 1rem;
}

/* Theme cards */
.theme-options {
  display: flex;
  gap: 1.5rem;
  justify-content: center;
}
.theme-card {
  width: 200px;
  border: 2px solid var(--border-color);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
  background: var(--bg-card);
}
.theme-card:hover {
  border-color: var(--border-light);
}
.theme-card.active {
  border-color: var(--accent-color);
}
.theme-preview {
  height: 120px;
  display: flex;
  flex-direction: column;
  padding: 12px;
  gap: 8px;
}
.theme-dark {
  background: #0a0a0a;
}
.theme-light {
  background: #f5f5f5;
}
.tp-bar {
  height: 10px;
  border-radius: 4px;
}
.theme-dark .tp-bar { background: rgba(255,255,255,0.1); }
.theme-light .tp-bar { background: rgba(0,0,0,0.08); }
.tp-body {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  flex: 1;
}
.tp-dot {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  flex-shrink: 0;
}
.theme-dark .tp-dot { background: rgba(255,255,255,0.15); }
.theme-light .tp-dot { background: rgba(0,0,0,0.1); }
.tp-line {
  flex: 1;
  height: 8px;
  border-radius: 4px;
  margin-top: 8px;
}
.theme-dark .tp-line { background: rgba(255,255,255,0.08); }
.theme-light .tp-line { background: rgba(0,0,0,0.06); }

.theme-label {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.8rem 0.5rem;
  font-size: 0.9rem;
}
.theme-check {
  font-size: 1rem;
  color: var(--accent-color);
  font-weight: 600;
}

/* Focus time style */
.pos-label {
  font-size: 0.8rem;
  opacity: 0.5;
  margin-bottom: 0.6rem;
}
.pos-options {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.2rem;
}
.pos-card {
  flex: 1;
  border: 2px solid var(--border-color);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
  background: var(--bg-card);
  text-align: center;
}
.pos-card:hover { border-color: var(--border-light); }
.pos-card.active { border-color: var(--accent-color); }
.pos-preview {
  height: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 8px;
  background: var(--bg-secondary);
}
.pos-preview-center { gap: 6px; }
.pos-preview-top { justify-content: flex-start; padding-top: 10px; }
.pos-preview-side .pp-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.pp-clock {
  font-family: monospace;
  font-size: 1.8rem;
  font-weight: 200;
  line-height: 1;
  color: var(--text-primary);
  opacity: 0.8;
}
.pp-clock-sm {
  font-family: monospace;
  font-size: 1.2rem;
  font-weight: 200;
  line-height: 1;
  color: var(--text-primary);
  opacity: 0.8;
}
.pp-controls {
  display: flex;
  gap: 4px;
}
.pp-controls span {
  display: block;
  width: 20px;
  height: 6px;
  border-radius: 3px;
  background: var(--accent-color);
  opacity: 0.5;
}
.pp-controls-stack { flex-direction: column; align-items: center; }
.pp-controls-stack span { width: 14px; height: 5px; }
.pos-name {
  display: block;
  padding: 0.5rem 0.3rem;
  font-size: 0.8rem;
}

/* Style rows */
.style-row {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.8rem;
  padding: 0.5rem 0.8rem;
  background: var(--panel-bg);
  backdrop-filter: blur(10px);
  border: 1px solid var(--border-color);
  border-radius: 12px;
}
.style-row label {
  font-size: 0.85rem;
  opacity: 0.7;
  min-width: 5em;
  flex-shrink: 0;
}
.style-select {
  flex: 1;
  padding: 0.45rem 0.7rem;
  background: var(--bg-hover);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-primary);
  font-size: 0.85rem;
  outline: none;
  cursor: pointer;
}
.style-select:focus { border-color: var(--accent-color); }
.style-select option { background: var(--bg-primary); color: var(--text-primary); }

.size-control {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 0.6rem;
}
.size-slider {
  flex: 1;
  -webkit-appearance: none;
  appearance: none;
  height: 4px;
  border-radius: 2px;
  background: var(--border-color);
  outline: none;
  cursor: pointer;
}
.size-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: var(--accent-color);
  cursor: pointer;
  transition: transform 0.15s;
}
.size-slider::-webkit-slider-thumb:hover { transform: scale(1.2); }
.size-slider::-moz-range-thumb {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: var(--accent-color);
  cursor: pointer;
  border: none;
}
.size-value {
  min-width: 3.5rem;
  font-size: 0.85rem;
  font-weight: 500;
  text-align: right;
  font-family: var(--font-mono);
  opacity: 0.8;
}
.btn-reset {
  background: none;
  border: 1px solid var(--border-color);
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 0.85rem;
  color: var(--text-muted);
  transition: all 0.2s;
}
.btn-reset:hover { color: var(--text-primary); border-color: var(--border-light); }

.color-area {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 0.6rem;
}
.color-presets {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
.color-dot {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;
  position: relative;
}
.color-dot.is-transparent {
  background: linear-gradient(45deg, #ddd 25%, transparent 25%, transparent 75%, #ddd 75%),
              linear-gradient(45deg, #ddd 25%, transparent 25%, transparent 75%, #ddd 75%);
  background-size: 8px 8px;
  background-position: 0 0, 4px 4px;
  border-color: var(--border-color);
}
.color-dot.is-transparent::after {
  content: '↺';
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.7rem;
  color: #666;
}
.color-dot:hover { transform: scale(1.15); }
.color-dot.active { border-color: var(--accent-color); transform: scale(1.15); }
.color-picker {
  width: 30px;
  height: 30px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  padding: 0;
  background: none;
}
.color-picker::-webkit-color-swatch-wrapper { padding: 0; }
.color-picker::-webkit-color-swatch { border: 2px solid var(--border-color); border-radius: 50%; }
.color-picker::-moz-color-swatch { border: 2px solid var(--border-color); border-radius: 50%; }

/* Wallpaper */
.upload-area {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  margin-bottom: 1rem;
}
.upload-btn {
  padding: 0.5rem 1.2rem;
  background: var(--accent-color);
  border: none;
  border-radius: 10px;
  color: var(--text-inverse);
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 500;
  transition: opacity 0.2s;
}
.upload-btn:hover { opacity: 0.85; }
.upload-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.upload-msg {
  font-size: 0.8rem;
  padding: 0.3rem 0.8rem;
  border-radius: 12px;
  animation: fadeIn 0.2s ease-out;
}
.upload-msg.ok { color: #166534; background: #dcfce7; }
.upload-msg.err { color: #991b1b; background: #fee2e2; }
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-3px); }
  to   { opacity: 1; transform: translateY(0); }
}

.wallpaper-list { display: flex; gap: 1rem; flex-wrap: wrap; }
.wallpaper-item {
  width: 120px;
  position: relative;
  border-radius: 12px;
  border: 2px solid transparent;
  overflow: hidden;
  transition: all 0.2s;
  text-align: center;
}
.wallpaper-item.active { border-color: var(--accent-color); }
.wallpaper-item:hover { opacity: 0.85; }
.wallpaper-preview-wrap {
  cursor: pointer;
}
.wallpaper-preview {
  width: 120px;
  height: 72px;
  border-radius: 8px;
  overflow: hidden;
  background: var(--bg-secondary);
}
.wallpaper-preview img { width: 100%; height: 100%; object-fit: cover; }
.wallpaper-name { display: block; margin-top: 0.3rem; font-size: 0.8rem; opacity: 0.7; }
.btn-del-wallpaper {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.5);
  border: none;
  border-radius: 50%;
  color: #fff;
  cursor: pointer;
  font-size: 0.65rem;
  opacity: 0;
  transition: opacity 0.2s;
}
.wallpaper-item:hover .btn-del-wallpaper { opacity: 1; }
.btn-del-wallpaper:hover { background: rgba(239,68,68,0.8); }
</style>
