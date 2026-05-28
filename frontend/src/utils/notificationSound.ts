/**
 * 播放一个安静的通知提示音（Web Audio API，无需音频文件）
 */

let audioCtx: AudioContext | null = null

function getAudioContext(): AudioContext | null {
  try {
    if (!audioCtx) {
      audioCtx = new AudioContext()
    }
    // 如果 AudioContext 被浏览器自动播放策略挂起，尝试恢复
    if (audioCtx.state === 'suspended') {
      audioCtx.resume()
    }
    return audioCtx
  } catch {
    return null
  }
}

export function playCompletionSound(): void {
  const ctx = getAudioContext()
  if (!ctx) return

  try {
    const gain = ctx.createGain()
    gain.connect(ctx.destination)
    // 音量轻柔，缓慢淡出
    gain.gain.setValueAtTime(0.15, ctx.currentTime)
    gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 2.2)

    const now = ctx.currentTime

    // 四音上行序列：C5 → E5 → G5 → C6，类似轻柔的 "do-mi-sol-do"
    const notes = [523, 659, 784, 1047]
    const noteDuration = 0.4
    notes.forEach((freq, i) => {
      const osc = ctx.createOscillator()
      osc.type = 'sine'
      const start = now + i * noteDuration
      osc.frequency.setValueAtTime(freq, start)
      osc.connect(gain)
      osc.start(start)
      osc.stop(start + noteDuration * 1.1)
    })
  } catch {
    // 浏览器不支持 AudioContext 时静默忽略
  }
}

/**
 * 播放一个更短促的单音提示（可用于倒计时最后几秒的提醒）
 */
export function playTickSound(): void {
  const ctx = getAudioContext()
  if (!ctx) return

  try {
    const gain = ctx.createGain()
    gain.connect(ctx.destination)
    gain.gain.setValueAtTime(0.03, ctx.currentTime)
    gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.15)

    const osc = ctx.createOscillator()
    osc.type = 'sine'
    osc.frequency.setValueAtTime(660, ctx.currentTime)
    osc.connect(gain)
    osc.start(ctx.currentTime)
    osc.stop(ctx.currentTime + 0.15)
  } catch {}
}
