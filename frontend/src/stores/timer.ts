import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { timerApi, type FocusSession } from '@/api/timer'
import { settingsApi } from '@/api/settings'
import { useFocusStyleStore } from '@/stores/focusStyle'
import { playCompletionSound } from '@/utils/notificationSound'

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
  const examMode = ref(false)
  const examMinutes = ref(0)
  let intervalId: ReturnType<typeof setInterval> | null = null

  // Exam review phase
  const examPhase = ref<'exam' | 'review' | null>(null)
  const reviewElapsed = ref(0)
  const wrongQuestions = ref('')
  const examTotalScore = ref(100)
  const examScore = ref(0)
  const examPaperName = ref('')
  const completedAsExam = ref(false)
  let reviewIntervalId: ReturnType<typeof setInterval> | null = null

  // 通知音效设置
  const notificationSoundEnabled = ref(true)

  const focusStyle = useFocusStyleStore()

  const isCountup = computed(() => focusStyle.style.timerMode === 'countup')
  const isExam = computed(() => examMode.value)

  async function loadDefaultTarget() {
    try {
      const res: any = await settingsApi.get('defaultFocusMinutes')
      if (res.data) {
        targetMinutes.value = parseInt(res.data)
      }
    } catch (_) {}
  }

  async function loadNotificationSetting() {
    try {
      const res: any = await settingsApi.get('notificationSound')
      if (res.data !== undefined && res.data !== null) {
        notificationSoundEnabled.value = res.data !== 'false'
      }
    } catch (_) {}
  }

  async function setNotificationEnabled(enabled: boolean) {
    notificationSoundEnabled.value = enabled
    await settingsApi.set('notificationSound', enabled ? 'true' : 'false')
  }

  const totalTargetSeconds = computed(() =>
    isCountup.value
      ? 59999 * 60
      : targetMinutes.value * 60 + targetSeconds.value
  )

  const formattedTime = computed(() => {
    if (isCountup.value) {
      const m = Math.floor(elapsedSeconds.value / 60)
      const s = elapsedSeconds.value % 60
      return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
    }
    const total = totalTargetSeconds.value - elapsedSeconds.value
    const m = Math.floor(Math.max(0, total) / 60)
    const s = Math.max(0, total) % 60
    return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
  })

  const reviewFormattedTime = computed(() => {
    const m = Math.floor(reviewElapsed.value / 60)
    const s = reviewElapsed.value % 60
    return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
  })

  const progress = computed(() => {
    if (isCountup.value) return 0
    const total = totalTargetSeconds.value
    return total > 0 ? elapsedSeconds.value / total : 0
  })

  function startTick() {
    stopTick()
    isRunning.value = true
    intervalId = setInterval(() => {
      elapsedSeconds.value++
      if (elapsedSeconds.value >= totalTargetSeconds.value) {
        if (notificationSoundEnabled.value) playCompletionSound()
        if (examMode.value && examPhase.value === 'exam') {
          stopTick()
          startReview()
        } else {
          completeSession()
        }
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

  function startReviewTick() {
    stopReviewTick()
    reviewIntervalId = setInterval(() => {
      reviewElapsed.value++
    }, 1000)
  }

  function stopReviewTick() {
    if (reviewIntervalId) {
      clearInterval(reviewIntervalId)
      reviewIntervalId = null
    }
  }

  function startReview() {
    examPhase.value = 'review'
    reviewElapsed.value = 0
    startReviewTick()
  }

  async function confirmReview() {
    stopReviewTick()
    completedAsExam.value = true
    // Capture exam data before clearing flags
    const examData = {
      examMode: true,
      examPaperName: examPaperName.value || undefined,
      wrongQuestions: wrongQuestions.value || undefined,
      examTotalScore: examTotalScore.value || undefined,
      examScore: examScore.value || undefined
    }
    examPhase.value = null
    examMode.value = false
    await completeSession(examData)
  }

  function cancelReview() {
    stopReviewTick()
    examPhase.value = null
    examMode.value = false
    wrongQuestions.value = ''
    examTotalScore.value = 100
    examScore.value = 0
    examPaperName.value = ''
    completedAsExam.value = false
    cancelSession()
  }

  async function startSession(subjectName?: string) {
    try {
      if (isCountup.value) {
        const res: any = await timerApi.start(0, 0, subjectName)
        currentSession.value = res.data
        elapsedSeconds.value = 0
        startTick()
      } else {
        const res: any = await timerApi.start(targetMinutes.value, targetSeconds.value, subjectName)
        currentSession.value = res.data
        elapsedSeconds.value = 0
        startTick()
      }
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

  async function completeSession(externalExamData?: {
    examMode?: boolean
    examPaperName?: string
    wrongQuestions?: string
    examTotalScore?: number
    examScore?: number
  }) {
    if (!currentSession.value) return
    try {
      const examData = externalExamData || (examMode.value ? {
        examMode: true,
        examPaperName: examPaperName.value || undefined,
        wrongQuestions: wrongQuestions.value || undefined,
        examTotalScore: examTotalScore.value || undefined,
        examScore: examScore.value || undefined
      } : undefined)
      await timerApi.complete(currentSession.value.id, elapsedSeconds.value, examData)
      stopTick()
      if (isCountup.value) {
        completedMinutes.value = Math.floor(elapsedSeconds.value / 60)
        completedSeconds.value = elapsedSeconds.value % 60
      } else {
        completedMinutes.value = targetMinutes.value
        completedSeconds.value = targetSeconds.value
      }
      currentSession.value = null
      elapsedSeconds.value = 0
      examMode.value = false
      justCompleted.value = true
    } catch (e) {
      console.error('Failed to complete session:', e)
    }
  }

  function acknowledgeCompletion() {
    justCompleted.value = false
    completedAsExam.value = false
  }

  async function cancelSession() {
    if (examMode.value && examPhase.value === 'exam') {
      stopTick()
      startReview()
      return
    }
    if (!currentSession.value) return
    try {
      await timerApi.cancel(currentSession.value.id)
      stopTick()
      currentSession.value = null
      elapsedSeconds.value = 0
      examMode.value = false
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

  function enterExamMode(minutes: number) {
    examMode.value = true
    examMinutes.value = minutes
    examPhase.value = null
    wrongQuestions.value = ''
    examTotalScore.value = 100
    examScore.value = 0
    examPaperName.value = ''
    completedAsExam.value = false
    setTarget(minutes)
  }

  function exitExamMode() {
    examMode.value = false
    examMinutes.value = 0
    examPhase.value = null
    stopReviewTick()
    wrongQuestions.value = ''
    examTotalScore.value = 100
    examScore.value = 0
    examPaperName.value = ''
    completedAsExam.value = false
  }

  return {
    currentSession, elapsedSeconds, targetMinutes, targetSeconds, isRunning,
    justCompleted, completedMinutes, completedSeconds, selectedSubject, totalTargetSeconds,
    formattedTime, progress, isCountup, isExam, examMode, examMinutes,
    examPhase, reviewElapsed, reviewFormattedTime,
    wrongQuestions, examTotalScore, examScore, examPaperName, completedAsExam,
    notificationSoundEnabled,
    startSession, pauseSession, resumeSession, completeSession,
    cancelSession, setTarget, setCustomTarget, stopTick, loadDefaultTarget, acknowledgeCompletion,
    enterExamMode, exitExamMode, startReview, confirmReview, cancelReview,
    loadNotificationSetting, setNotificationEnabled
  }
})
