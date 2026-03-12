import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import { savePomodoro, getTodayPomodoroCount } from '@/api/pomodoro'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

export const usePomodoroStore = defineStore('pomodoro', () => {
  const userStore = useUserStore()
  
  // -- Persisted Settings --
  const pomodoroDuration = ref(parseInt(localStorage.getItem('pomodoro_duration_work')) || 25 * 60)
  const shortBreakDuration = ref(parseInt(localStorage.getItem('pomodoro_duration_short')) || 5 * 60)
  const longBreakDuration = ref(parseInt(localStorage.getItem('pomodoro_duration_long')) || 15 * 60)
  
  const autoStartBreaks = ref(localStorage.getItem('pomodoro_autoStartBreaks') === 'true')
  const autoStartPomodoros = ref(localStorage.getItem('pomodoro_autoStartPomodoros') === 'true')
  const longBreakInterval = ref(parseInt(localStorage.getItem('pomodoro_longBreakInterval')) || 4)
  
  // Sound Settings
  const soundEnabled = ref(localStorage.getItem('pomodoro_soundEnabled') !== 'false')
  const soundVolume = ref(parseInt(localStorage.getItem('pomodoro_soundVolume')) || 50)
  const selectedSound = ref(localStorage.getItem('pomodoro_selectedSound') || 'bell')
  
  // Background Settings (Global or Hero specific)
  const backgroundImage = ref(localStorage.getItem('pomodoro_backgroundImage') || '')
  const heroTheme = ref(localStorage.getItem('pomodoro_heroTheme') || 'auto') // 'auto' | 'light' | 'dark'
  const bgOverlayOpacity = ref(parseFloat(localStorage.getItem('pomodoro_bgOverlayOpacity')) || 0.2)

  // -- State --
  const isRunning = ref(false)
  const currentMode = ref(localStorage.getItem('pomodoro_currentMode') || 'pomodoro') // 'pomodoro' | 'short-break' | 'long-break'
  const timeLeft = ref(parseInt(localStorage.getItem('pomodoro_timeLeft')) || pomodoroDuration.value)
  const endTime = ref(parseInt(localStorage.getItem('pomodoro_endTime')) || null)
  
  const completedPomodoros = ref(0)
  const todayFocusSeconds = ref(0)
  const selectedTaskId = ref(null)
  const pomodorosSinceLongBreak = ref(parseInt(localStorage.getItem('pomodoro_pomodorosSinceLongBreak')) || 0)

  let timerInterval = null

  // -- Computed --
  const formattedTime = computed(() => {
    const minutes = Math.floor(timeLeft.value / 60)
    const seconds = timeLeft.value % 60
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
  })

  const currentDuration = computed(() => {
    if (currentMode.value === 'short-break') return shortBreakDuration.value
    if (currentMode.value === 'long-break') return longBreakDuration.value
    return pomodoroDuration.value
  })

  const progressPercentage = computed(() => {
    const total = currentDuration.value
    if (total === 0) return 0
    return ((total - timeLeft.value) / total) * 100
  })

  // -- Watchers for persistence --
  watch(pomodoroDuration, (val) => localStorage.setItem('pomodoro_duration_work', val))
  watch(shortBreakDuration, (val) => localStorage.setItem('pomodoro_duration_short', val))
  watch(longBreakDuration, (val) => localStorage.setItem('pomodoro_duration_long', val))
  watch(autoStartBreaks, (val) => localStorage.setItem('pomodoro_autoStartBreaks', val))
  watch(autoStartPomodoros, (val) => localStorage.setItem('pomodoro_autoStartPomodoros', val))
  watch(soundEnabled, (val) => localStorage.setItem('pomodoro_soundEnabled', val))
  watch(soundVolume, (val) => localStorage.setItem('pomodoro_soundVolume', val))
  watch(selectedSound, (val) => localStorage.setItem('pomodoro_selectedSound', val))
  watch(backgroundImage, (val) => localStorage.setItem('pomodoro_backgroundImage', val))
  watch(heroTheme, (val) => localStorage.setItem('pomodoro_heroTheme', val))
  watch(bgOverlayOpacity, (val) => localStorage.setItem('pomodoro_bgOverlayOpacity', val))
  watch(currentMode, (val) => localStorage.setItem('pomodoro_currentMode', val))
  watch(pomodorosSinceLongBreak, (val) => localStorage.setItem('pomodoro_pomodorosSinceLongBreak', val))

  // Restore logic
  if (endTime.value && endTime.value > Date.now()) {
    isRunning.value = true
    startTimer(true)
  } else if (endTime.value && endTime.value <= Date.now()) {
    timeLeft.value = 0
    isRunning.value = false
    localStorage.removeItem('pomodoro_endTime')
    // Ideally we should process completion here too if app was closed
  }

  // -- Actions --
  
  function setMode(mode) {
    pauseTimer()
    currentMode.value = mode
    // Reset time to full duration of that mode
    timeLeft.value = currentDuration.value
    localStorage.setItem('pomodoro_timeLeft', timeLeft.value)
  }

  function startTimer(resume = false) {
    if (isRunning.value && !resume) return
    
    isRunning.value = true
    
    if (!resume) {
      endTime.value = Date.now() + timeLeft.value * 1000
      localStorage.setItem('pomodoro_endTime', endTime.value)
    }

    if (timerInterval) clearInterval(timerInterval)

    timerInterval = setInterval(() => {
      const now = Date.now()
      const remaining = Math.round((endTime.value - now) / 1000)
      
      if (remaining >= 0) {
        timeLeft.value = remaining
        localStorage.setItem('pomodoro_timeLeft', timeLeft.value)
      } else {
        timeLeft.value = 0
        completeSession()
        clearInterval(timerInterval)
      }
    }, 1000)
  }

  function pauseTimer() {
    isRunning.value = false
    if (timerInterval) {
      clearInterval(timerInterval)
      timerInterval = null
    }
    localStorage.removeItem('pomodoro_endTime')
    localStorage.setItem('pomodoro_timeLeft', timeLeft.value)
  }

  function resetTimer() {
    pauseTimer()
    timeLeft.value = currentDuration.value
    localStorage.setItem('pomodoro_timeLeft', timeLeft.value)
  }

  async function completeSession() {
    pauseTimer()
    localStorage.removeItem('pomodoro_endTime')
    
    // Play sound
    if (soundEnabled.value) {
      playSound()
    }

    if (currentMode.value === 'pomodoro') {
      // Completed a pomodoro
      pomodorosSinceLongBreak.value++
      
      // Save record
      if (userStore.user?.id) {
        try {
          await savePomodoro({
            userId: userStore.user.id,
            taskId: selectedTaskId.value,
            durationSeconds: pomodoroDuration.value,
            status: 'COMPLETED'
          })
          fetchTodayCount()
        } catch (e) { console.error(e) }
      } else {
        completedPomodoros.value++
      }

      // Auto-switch logic
      if (pomodorosSinceLongBreak.value >= longBreakInterval.value) {
        setMode('long-break')
        pomodorosSinceLongBreak.value = 0
      } else {
        setMode('short-break')
      }

      if (autoStartBreaks.value) {
        startTimer()
      } else {
        ElMessage.success('Pomodoro finished! Take a break.')
      }

    } else {
      // Break finished
      setMode('pomodoro')
      if (autoStartPomodoros.value) {
        startTimer()
      } else {
        ElMessage.info('Break is over! Ready to focus?')
      }
    }
  }

  function playSound() {
    // Simple implementation - can be improved with AudioContext or preloaded assets
    const audio = new Audio(`/sounds/${selectedSound.value}.mp3`)
    audio.volume = soundVolume.value / 100
    audio.play().catch(e => console.log('Audio play failed', e))
  }

  // Update duration from settings
  function updateSettings(settings) {
    if (settings.pomodoro) pomodoroDuration.value = settings.pomodoro * 60
    if (settings.shortBreak) shortBreakDuration.value = settings.shortBreak * 60
    if (settings.longBreak) longBreakDuration.value = settings.longBreak * 60
    
    // If not running, update current timeLeft to reflect new duration
    if (!isRunning.value) {
      timeLeft.value = currentDuration.value
    }
  }

  async function fetchTodayCount() {
    if (userStore.user?.id) {
      try {
        const res = await getTodayPomodoroCount(userStore.user.id)
        if (res) {
          completedPomodoros.value = res.count || 0
          todayFocusSeconds.value = res.totalSeconds || 0
        }
      } catch (e) {}
    }
  }

  function toggleTimer() {
    if (isRunning.value) {
      pauseTimer()
    } else {
      startTimer()
    }
  }

  return {
    // Settings
    pomodoroDuration,
    shortBreakDuration,
    longBreakDuration,
    autoStartBreaks,
    autoStartPomodoros,
    longBreakInterval,
    soundEnabled,
    soundVolume,
    selectedSound,
    backgroundImage,
    heroTheme,
    bgOverlayOpacity,
    
    // State
    isRunning,
    currentMode,
    timeLeft,
    formattedTime,
    progressPercentage,
    completedPomodoros,
    todayFocusSeconds,
    selectedTaskId,
    
    // Actions
    toggleTimer,
    startTimer,
    pauseTimer,
    resetTimer,
    setMode,
    updateSettings,
    fetchTodayCount
  }
})
