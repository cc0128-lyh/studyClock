import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { timerApi, type FocusSession } from '@/api/timer'
import { settingsApi } from '@/api/settings'

export const useTimerStore = defineStore('timer', () => {
  const currentSession = ref<FocusSession | null>(null)
  const elapsedSeconds = ref(0)
  const targetMinutes = ref(25)
  const targetSeconds = ref(0)
  const isRunning = ref(false)
  const justCompleted = ref(false)
  const completedMinutes = ref(0)
  const completedSeconds = ref(0)
  const selectedSubject = ref('')
  let intervalId: ReturnType<typeof setInterval> | null = null

  async function loadDefaultTarget() {
    try {
      const res: any = await settingsApi.get('defaultFocusMinutes')
      if (res.data) {
        targetMinutes.value = parseInt(res.data)
      }
    } catch (_) {}
  }

  const totalTargetSeconds = computed(() =>
    targetMinutes.value * 60 + targetSeconds.value
  )

  const formattedTime = computed(() => {
    const total = totalTargetSeconds.value - elapsedSeconds.value
    const m = Math.floor(Math.max(0, total) / 60)
    const s = Math.max(0, total) % 60
    return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
  })

  const progress = computed(() => {
    const total = totalTargetSeconds.value
    return total > 0 ? elapsedSeconds.value / total : 0
  })

  function startTick() {
    stopTick()
    isRunning.value = true
    intervalId = setInterval(() => {
      elapsedSeconds.value++
      if (elapsedSeconds.value >= totalTargetSeconds.value) {
        completeSession()
      }
    }, 1000)
  }

  function stopTick() {
    if (intervalId) {
      clearInterval(intervalId)
      intervalId = null
    }
    isRunning.value = false
  }

  async function startSession(subjectName?: string) {
    try {
      const res: any = await timerApi.start(targetMinutes.value, targetSeconds.value, subjectName)
      currentSession.value = res.data
      elapsedSeconds.value = 0
      startTick()
    } catch (e) {
      console.error('Failed to start session:', e)
    }
  }

  async function pauseSession() {
    if (!currentSession.value) return
    try {
      await timerApi.pause(currentSession.value.id)
      stopTick()
    } catch (e) {
      console.error('Failed to pause session:', e)
    }
  }

  async function resumeSession() {
    if (!currentSession.value) return
    try {
      await timerApi.resume(currentSession.value.id)
      startTick()
    } catch (e) {
      console.error('Failed to resume session:', e)
    }
  }

  async function completeSession() {
    if (!currentSession.value) return
    try {
      await timerApi.complete(currentSession.value.id, elapsedSeconds.value)
      stopTick()
      completedMinutes.value = targetMinutes.value
      completedSeconds.value = targetSeconds.value
      currentSession.value = null
      elapsedSeconds.value = 0
      justCompleted.value = true
    } catch (e) {
      console.error('Failed to complete session:', e)
    }
  }

  function acknowledgeCompletion() {
    justCompleted.value = false
  }

  async function cancelSession() {
    if (!currentSession.value) return
    try {
      await timerApi.cancel(currentSession.value.id)
      stopTick()
      currentSession.value = null
      elapsedSeconds.value = 0
    } catch (e) {
      console.error('Failed to cancel session:', e)
    }
  }

  function setTarget(minutes: number) {
    if (!isRunning.value) {
      targetMinutes.value = minutes
      targetSeconds.value = 0
    }
  }

  function setCustomTarget(minutes: number, seconds: number) {
    if (!isRunning.value) {
      targetMinutes.value = minutes
      targetSeconds.value = seconds
    }
  }

  return {
    currentSession, elapsedSeconds, targetMinutes, targetSeconds, isRunning,
    justCompleted, completedMinutes, completedSeconds, selectedSubject, totalTargetSeconds,
    formattedTime, progress,
    startSession, pauseSession, resumeSession, completeSession,
    cancelSession, setTarget, setCustomTarget, stopTick, loadDefaultTarget, acknowledgeCompletion
  }
})
