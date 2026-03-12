<template>
  <div class="settings-dialog-container">
    <div class="sidebar">
      <div 
        class="menu-item" 
        :class="{ active: activeTab === 'general' }"
        @click="activeTab = 'general'"
      >
        <el-icon><Monitor /></el-icon> General
      </div>
      <div 
        class="menu-item" 
        :class="{ active: activeTab === 'timers' }"
        @click="activeTab = 'timers'"
      >
        <el-icon><Timer /></el-icon> Timers
      </div>
      <div 
        class="menu-item" 
        :class="{ active: activeTab === 'sounds' }"
        @click="activeTab = 'sounds'"
      >
        <el-icon><Headset /></el-icon> Sounds
      </div>
    </div>
    
    <div class="content">
      <!-- General: Background Image -->
      <div v-if="activeTab === 'general'" class="tab-pane">
        <h3>Background</h3>
        <div class="bg-grid">
          <div 
            class="bg-item" 
            :class="{ active: !tempBg }"
            @click="tempBg = ''"
          >
            <div class="bg-preview default-bg"></div>
            <span>Default</span>
          </div>
          
          <div 
            v-for="bg in backgrounds" 
            :key="bg" 
            class="bg-item"
            :class="{ active: tempBg === bg }"
            @click="tempBg = bg"
          >
            <div 
              class="bg-preview" 
              :style="{ backgroundImage: `url(/backgrounds/${bg})` }"
            ></div>
            <span>{{ formatBgName(bg) }}</span>
          </div>
        </div>

        <div v-if="tempBg" class="mt-4">
          <h3>Appearance</h3>
          <div class="form-col">
            <div class="form-group full-width">
              <label>Hero Text Theme</label>
              <el-radio-group v-model="heroTheme" size="small">
                <el-radio-button label="auto">Auto</el-radio-button>
                <el-radio-button label="light">Light Text</el-radio-button>
                <el-radio-button label="dark">Dark Text</el-radio-button>
              </el-radio-group>
            </div>
            <div class="form-group full-width">
              <div class="flex-between">
                <label>Background Overlay Opacity</label>
                <span>{{ Math.round(bgOverlayOpacity * 100) }}%</span>
              </div>
              <el-slider v-model="bgOverlayOpacity" :min="0" :max="1" :step="0.05" />
            </div>
          </div>
        </div>
      </div>

      <!-- Timers: Duration & Auto-start -->
      <div v-if="activeTab === 'timers'" class="tab-pane">
        <h3>Timer Durations (minutes)</h3>
        <div class="form-row">
          <div class="form-group">
            <label>Pomodoro</label>
            <el-input-number v-model="workMins" :min="1" :max="60" />
          </div>
          <div class="form-group">
            <label>Short Break</label>
            <el-input-number v-model="shortBreakMins" :min="1" :max="30" />
          </div>
          <div class="form-group">
            <label>Long Break</label>
            <el-input-number v-model="longBreakMins" :min="1" :max="60" />
          </div>
        </div>
        
        <h3>Sequence</h3>
        <div class="form-col">
          <div class="switch-row">
            <span>Auto-start Breaks</span>
            <el-switch v-model="autoStartBreaks" />
          </div>
          <div class="switch-row">
            <span>Auto-start Pomodoros</span>
            <el-switch v-model="autoStartPomodoros" />
          </div>
          <div class="form-group mt-2">
            <label>Long Break interval</label>
            <el-input-number v-model="longBreakInterval" :min="1" :max="10" />
          </div>
        </div>
      </div>

      <!-- Sounds: Volume & Selection -->
      <div v-if="activeTab === 'sounds'" class="tab-pane">
        <h3>Sound Settings</h3>
        <div class="form-col">
           <div class="form-group full-width">
            <label>Alert Sound</label>
            <el-select v-model="selectedSound">
              <el-option label="Bell" value="bell" />
              <el-option label="Alarm" value="alarm" />
              <el-option label="Digital" value="digital" />
            </el-select>
          </div>
          
          <div class="form-group full-width mt-2">
            <div class="flex-between">
              <label>Volume</label>
              <span>{{ soundVolume }}%</span>
            </div>
            <el-slider v-model="soundVolume" />
          </div>
          
          <div class="switch-row mt-2">
            <span>Play sound when timer finishes</span>
            <el-switch v-model="soundEnabled" />
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <div class="dialog-footer">
    <el-button @click="$emit('close')">Close</el-button>
    <el-button type="primary" @click="saveChanges">Save Changes</el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { usePomodoroStore } from '@/stores/pomodoro'
import { Monitor, Timer, Headset } from '@element-plus/icons-vue'

const emit = defineEmits(['close'])
const pomodoroStore = usePomodoroStore()
const activeTab = ref('general')

// Local state for optimistic UI
const workMins = ref(Math.floor(pomodoroStore.pomodoroDuration / 60))
const shortBreakMins = ref(Math.floor(pomodoroStore.shortBreakDuration / 60))
const longBreakMins = ref(Math.floor(pomodoroStore.longBreakDuration / 60))

const autoStartBreaks = ref(pomodoroStore.autoStartBreaks)
const autoStartPomodoros = ref(pomodoroStore.autoStartPomodoros)
const longBreakInterval = ref(pomodoroStore.longBreakInterval)

const soundEnabled = ref(pomodoroStore.soundEnabled)
const soundVolume = ref(pomodoroStore.soundVolume)
const selectedSound = ref(pomodoroStore.selectedSound)

const tempBg = ref(pomodoroStore.backgroundImage)
const heroTheme = ref(pomodoroStore.heroTheme)
const bgOverlayOpacity = ref(pomodoroStore.bgOverlayOpacity)

const backgrounds = ['day_1.jpg', 'night_1.jpg']

const formatBgName = (name) => {
  return name.replace('.jpg', '').replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase())
}

const saveChanges = () => {
  // Commit changes to store
  pomodoroStore.pomodoroDuration = workMins.value * 60
  pomodoroStore.shortBreakDuration = shortBreakMins.value * 60
  pomodoroStore.longBreakDuration = longBreakMins.value * 60
  
  pomodoroStore.autoStartBreaks = autoStartBreaks.value
  pomodoroStore.autoStartPomodoros = autoStartPomodoros.value
  pomodoroStore.longBreakInterval = longBreakInterval.value
  
  pomodoroStore.soundEnabled = soundEnabled.value
  pomodoroStore.soundVolume = soundVolume.value
  pomodoroStore.selectedSound = selectedSound.value
  
  pomodoroStore.backgroundImage = tempBg.value
  pomodoroStore.heroTheme = heroTheme.value
  pomodoroStore.bgOverlayOpacity = bgOverlayOpacity.value
  
  // If timer is not running, reset it to reflect new settings
  if (!pomodoroStore.isRunning) {
    pomodoroStore.updateSettings({
      pomodoro: workMins.value,
      shortBreak: shortBreakMins.value,
      longBreak: longBreakMins.value
    })
  }
  
  emit('close')
}
</script>

<style scoped>
.settings-dialog-container {
  display: flex;
  height: 450px;
  border-top: 1px solid var(--el-border-color-lighter);
  border-bottom: 1px solid var(--el-border-color-lighter);
  margin: -20px -20px 0 -20px; /* Offset dialog padding */
}

.sidebar {
  width: 180px;
  border-right: 1px solid var(--el-border-color-lighter);
  padding: 20px 0;
  background-color: var(--el-fill-color-light);
  display: flex;
  flex-direction: column;
}

.menu-item {
  padding: 12px 24px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--el-text-color-regular);
  transition: all 0.2s;
  font-weight: 500;
}

.menu-item:hover {
  background-color: var(--el-fill-color);
}

.menu-item.active {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  border-right: 3px solid var(--el-color-primary);
}

.content {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
}

.tab-pane h3 {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.bg-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 20px;
}

.bg-item {
  cursor: pointer;
  text-align: center;
}

.bg-preview {
  width: 100%;
  height: 90px;
  border-radius: 8px;
  background-size: cover;
  background-position: center;
  border: 3px solid transparent;
  margin-bottom: 8px;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.bg-preview.default-bg {
  background: #1a1a1a;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.bg-item.active .bg-preview {
  border-color: var(--el-color-primary);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.3);
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.form-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.form-group.full-width {
  width: 100%;
}

.form-group label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  font-weight: 500;
}

.switch-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.flex-between {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.mt-2 {
  margin-top: 16px;
}

.mt-4 {
  margin-top: 32px;
}

.dialog-footer {
  padding: 20px 24px 0;
  text-align: right;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
