import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const usePomodoroStore = defineStore('pomodoro', () => {
  // Configurable settings
  const workDuration = ref(25 * 60) // 25 minutes in seconds
  const breakDuration = ref(5 * 60) // 5 minutes in seconds
  
  // State
  const timeLeft = ref(workDuration.value)
  const isRunning = ref(false)
  const isWorkSession = ref(true) // true = work, false = break
  const completedPomodoros = ref(0)
  
  let timerInterval = null

  // Computed
  const formattedTime = computed(() => {
    const minutes = Math.floor(timeLeft.value / 60)
    const seconds = timeLeft.value % 60
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
  })

  const progressPercentage = computed(() => {
    const total = isWorkSession.value ? workDuration.value : breakDuration.value
    return ((total - timeLeft.value) / total) * 100
  })

  // Actions
  function startTimer() {
    if (isRunning.value) return
    
    isRunning.value = true
    timerInterval = setInterval(() => {
      if (timeLeft.value > 0) {
        timeLeft.value--
      } else {
        completeSession()
      }
    }, 1000)
  }

  function pauseTimer() {
    isRunning.value = false
    if (timerInterval) {
      clearInterval(timerInterval)
      timerInterval = null
    }
  }

  function resetTimer() {
    pauseTimer()
    timeLeft.value = isWorkSession.value ? workDuration.value : breakDuration.value
  }

  function toggleTimer() {
    if (isRunning.value) {
      pauseTimer()
    } else {
      startTimer()
    }
  }

  function completeSession() {
    pauseTimer()
    // Play sound or notification here (future enhancement)
    
    if (isWorkSession.value) {
      completedPomodoros.value++
      // Auto-switch to break? Or wait for user? Let's wait for user for now, but flip the mode
      isWorkSession.value = false
      timeLeft.value = breakDuration.value
    } else {
      // Break over, back to work
      isWorkSession.value = true
      timeLeft.value = workDuration.value
    }
  }

  function setMode(mode) {
    pauseTimer()
    isWorkSession.value = mode === 'work'
    timeLeft.value = isWorkSession.value ? workDuration.value : breakDuration.value
  }

  return {
    timeLeft,
    isRunning,
    isWorkSession,
    completedPomodoros,
    formattedTime,
    progressPercentage,
    startTimer,
    pauseTimer,
    resetTimer,
    toggleTimer,
    setMode
  }
})
