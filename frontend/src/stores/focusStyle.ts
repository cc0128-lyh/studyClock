import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export type FocusPosition = 'center' | 'top' | 'side'
export type TimerMode = 'countdown' | 'countup'

export interface FocusStyle {
  position: FocusPosition
  fontFamily: string
  fontSize: number
  color: string
  timerMode: TimerMode
}

const STORAGE_KEY = 'focusStyle'

const DEFAULTS: FocusStyle = {
  position: 'center',
  fontFamily: '',
  fontSize: 10,
  color: '',
  timerMode: 'countdown'
}

function load(): FocusStyle {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (raw) return { ...DEFAULTS, ...JSON.parse(raw) }
  } catch {}
  return { ...DEFAULTS }
}

function save(s: FocusStyle) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(s))
}

export const useFocusStyleStore = defineStore('focusStyle', () => {
  const style = ref<FocusStyle>(load())

  function setPosition(p: FocusPosition) { style.value.position = p }
  function setFontFamily(f: string) { style.value.fontFamily = f }
  function setFontSize(s: number) { style.value.fontSize = s }
  function setColor(c: string) { style.value.color = c }
  function setTimerMode(m: TimerMode) { style.value.timerMode = m }

  watch(style, () => save(style.value), { deep: true })

  return { style, setPosition, setFontFamily, setFontSize, setColor, setTimerMode }
})
