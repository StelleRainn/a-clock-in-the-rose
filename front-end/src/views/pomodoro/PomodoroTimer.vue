<template>
  <div class="pomodoro-container" :class="{ 'immersive-mode': isImmersive }">
    <el-card class="timer-card" :class="{ 'immersive-card': isImmersive }">
      <template #header v-if="!isImmersive">
        <div class="card-header">
          <el-radio-group v-model="sessionMode" @change="handleModeChange">
            <el-radio-button label="work" value="work">Focus</el-radio-button>
            <el-radio-button label="break" value="break">Break</el-radio-button>
          </el-radio-group>
          <el-button link @click="toggleImmersive">
            <el-icon><FullScreen /></el-icon>
          </el-button>
        </div>
      </template>

      <!-- Immersive Header -->
      <div v-if="isImmersive" class="immersive-header">
        <el-button link class="exit-btn" @click="toggleImmersive">
          <el-icon><Close /></el-icon> Exit Focus Mode
        </el-button>
      </div>

      <div class="timer-display">
        <el-progress 
          type="dashboard" 
          :percentage="pomodoroStore.progressPercentage" 
          :width="isImmersive ? 300 : 200"
          :stroke-width="isImmersive ? 15 : 6"
          :color="progressColor"
        >
          <template #default>
            <h1 class="timer-text" :class="{ 'immersive-text': isImmersive }">{{ pomodoroStore.formattedTime }}</h1>
          </template>
        </el-progress>
      </div>

      <div class="slider-container" v-if="!isImmersive">
        <span class="duration-label">Duration (min)</span>
        <el-slider 
          v-model="sliderValue" 
          :min="1" 
          :max="60" 
          :disabled="pomodoroStore.isRunning"
          @input="handleSliderChange"
        />
      </div>

      <!-- Task Selection Dropdown -->
      <div class="task-selector" v-if="!isImmersive && pomodoroStore.isWorkSession">
        <el-select 
          v-model="selectedTask" 
          placeholder="Select a task to focus on" 
          clearable 
          filterable
          :disabled="pomodoroStore.isRunning"
          @change="handleTaskSelect"
        >
          <el-option
            v-for="task in tasks"
            :key="task.id"
            :label="task.title"
            :value="task.id"
          >
            <span style="float: left">{{ task.title }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px">
              <el-tag size="small" :type="getPriorityType(task.priority)">{{ task.priority }}</el-tag>
            </span>
          </el-option>
        </el-select>
      </div>
      
      <!-- Immersive Task Display -->
      <div v-if="isImmersive && currentTaskTitle" class="immersive-task">
        <p>Focusing on: <strong>{{ currentTaskTitle }}</strong></p>
      </div>

      <div class="status-text">
        <span v-if="pomodoroStore.isRunning">Running...</span>
        <span v-else>Paused</span>
      </div>

      <div class="timer-controls">
        <el-button 
          v-if="!pomodoroStore.isRunning" 
          type="primary" 
          :size="isImmersive ? 'large' : 'large'" 
          :style="isImmersive ? 'transform: scale(1.5)' : ''"
          circle 
          @click="pomodoroStore.startTimer()"
        >
          <el-icon><VideoPlay /></el-icon>
        </el-button>
        <el-button 
          v-else 
          type="warning" 
          :size="isImmersive ? 'large' : 'large'"
          :style="isImmersive ? 'transform: scale(1.5)' : ''" 
          circle 
          @click="pomodoroStore.pauseTimer"
        >
          <el-icon><VideoPause /></el-icon>
        </el-button>
        <el-button 
          size="large" 
          circle 
          @click="pomodoroStore.resetTimer"
          v-if="!isImmersive || !pomodoroStore.isRunning"
        >
          <el-icon><Refresh /></el-icon>
        </el-button>
      </div>
      
      <!-- White Noise Controls -->
      <div class="noise-controls" v-if="isImmersive">
        <p>White Noise</p>
        <el-radio-group v-model="currentNoise" size="small" @change="playNoise">
          <el-radio-button label="none" value="none">Off</el-radio-button>
          <el-radio-button label="rain" value="rain">Rain</el-radio-button>
          <el-radio-button label="cafe" value="cafe">Cafe</el-radio-button>
        </el-radio-group>
      </div>

      <div class="stats-footer" v-if="!isImmersive">
        <p>Completed Pomodoros Today: {{ pomodoroStore.completedPomodoros }}</p>
      </div>
    </el-card>
    
    <!-- Hidden Audio Elements -->
    <audio ref="rainAudio" loop src="https://assets.mixkit.co/active_storage/sfx/2496/2496-preview.mp3"></audio>
    <audio ref="cafeAudio" loop src="https://assets.mixkit.co/active_storage/sfx/248/248-preview.mp3"></audio>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue'
import { usePomodoroStore } from '@/stores/pomodoro'
import { useUserStore } from '@/stores/user'
import { getTasks } from '@/api/task'

const pomodoroStore = usePomodoroStore()
const userStore = useUserStore()
const sessionMode = ref('work')
const sliderValue = ref(25)
const isImmersive = ref(false)
const currentNoise = ref('none')
const rainAudio = ref(null)
const cafeAudio = ref(null)
const tasks = ref([])
const selectedTask = ref(pomodoroStore.selectedTaskId)

// Sync sessionMode with store state
watch(() => pomodoroStore.isWorkSession, (isWork) => {
  sessionMode.value = isWork ? 'work' : 'break'
  // Update slider value based on current duration setting
  const duration = isWork ? pomodoroStore.workDuration : pomodoroStore.breakDuration
  sliderValue.value = Math.floor(duration / 60)
}, { immediate: true })

const handleModeChange = (val) => {
  pomodoroStore.setMode(val)
}

const handleSliderChange = (val) => {
  pomodoroStore.updateDuration(val * 60)
}

const handleTaskSelect = (val) => {
  pomodoroStore.selectTask(val)
}

const toggleImmersive = () => {
  isImmersive.value = !isImmersive.value
  // Optional: Enter browser fullscreen
  if (isImmersive.value) {
    document.documentElement.requestFullscreen().catch(e => {})
  } else {
    if (document.fullscreenElement) {
      document.exitFullscreen().catch(e => {})
    }
  }
}

const playNoise = (val) => {
  // Stop all first
  rainAudio.value?.pause()
  cafeAudio.value?.pause()
  
  if (val === 'rain') {
    rainAudio.value?.play()
  } else if (val === 'cafe') {
    cafeAudio.value?.play()
  }
}

const fetchUserTasks = async () => {
  if (userStore.user && userStore.user.id) {
    try {
      // Fetch only IN_PROGRESS or TODO tasks ideally, but for now fetch all
      const data = await getTasks(userStore.user.id)
      tasks.value = data.filter(t => t.status !== 'DONE') || []
    } catch (e) {
      console.error(e)
    }
  }
}

const currentTaskTitle = computed(() => {
  if (!pomodoroStore.selectedTaskId) return null
  const task = tasks.value.find(t => t.id === pomodoroStore.selectedTaskId)
  return task ? task.title : null
})

const getPriorityType = (priority) => {
  const map = {
    'HIGH': 'danger',
    'MEDIUM': 'warning',
    'LOW': 'info'
  }
  return map[priority] || ''
}

const progressColor = computed(() => {
  return pomodoroStore.isWorkSession ? '#409eff' : '#67c23a'
})

onMounted(() => {
  pomodoroStore.fetchTodayCount()
  fetchUserTasks()
  // Initialize slider value correctly on mount
  const duration = pomodoroStore.isWorkSession ? pomodoroStore.workDuration : pomodoroStore.breakDuration
  sliderValue.value = Math.floor(duration / 60)
})
</script>

<style scoped>
.pomodoro-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  transition: all 0.3s ease;
}

.pomodoro-container.immersive-mode {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: #1a1a1a;
  z-index: 9999;
}

.timer-card {
  width: 400px;
  text-align: center;
  padding: 20px;
  transition: all 0.3s ease;
}

.timer-card.immersive-card {
  width: 100%;
  height: 100%;
  background: transparent;
  border: none;
  box-shadow: none;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.timer-display {
  margin: 20px 0;
  display: flex;
  justify-content: center;
}

.timer-text {
  font-size: 2.5rem;
  margin: 0;
  color: #303133;
}

.immersive-text {
  font-size: 4rem;
  color: white;
}

.slider-container {
  padding: 0 20px;
  margin-bottom: 20px;
}

.duration-label {
  font-size: 12px;
  color: #909399;
  display: block;
  margin-bottom: 5px;
  text-align: left;
}

.task-selector {
  margin-bottom: 20px;
  padding: 0 20px;
}

.timer-controls {
  margin-top: 20px;
  margin-bottom: 20px;
}

.status-text {
  color: #909399;
  font-size: 14px;
  margin-bottom: 10px;
}

.stats-footer {
  margin-top: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 10px;
  color: #606266;
}

.immersive-header {
  position: absolute;
  top: 20px;
  right: 20px;
}

.immersive-task {
  margin-bottom: 20px;
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.9);
}

.exit-btn {
  color: rgba(255, 255, 255, 0.7);
  font-size: 16px;
}

.exit-btn:hover {
  color: white;
}

.noise-controls {
  margin-top: 40px;
  color: rgba(255, 255, 255, 0.8);
}

.noise-controls p {
  margin-bottom: 10px;
  font-size: 14px;
}
</style>
