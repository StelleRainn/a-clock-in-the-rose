<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>Dashboard</h1>
      <span class="date-display">{{ currentDate }}</span>
    </div>
    
    <el-row :gutter="20">
      <!-- Left Column: Tasks & Calendar -->
      <el-col :span="16">
        <!-- Calendar View -->
        <el-card class="calendar-card">
          <el-calendar v-model="calendarDate">
            <template #date-cell="{ data }">
              <div class="calendar-cell" :class="{ 'is-selected': data.isSelected }">
                <div class="day-number">{{ data.day.split('-').slice(2).join('') }}</div>
                <div class="day-content">
                  <div v-if="getTasksForDate(data.day).length > 0" class="task-dot-container">
                    <el-tooltip 
                      effect="dark" 
                      :content="`${getTasksForDate(data.day).length} tasks due`" 
                      placement="top"
                    >
                      <span class="task-dot"></span>
                    </el-tooltip>
                  </div>
                  <div v-if="getFocusTimeForDate(data.day) > 0" class="focus-badge">
                    {{ Math.round(getFocusTimeForDate(data.day) / 60) }}m
                  </div>
                  <!-- Streak Flame Icon -->
                  <div v-if="isStreakDay(data.day)" class="streak-icon">
                    <el-icon color="#f56c6c"><Opportunity /></el-icon>
                  </div>
                </div>
              </div>
            </template>
          </el-calendar>
        </el-card>

        <!-- Recent Tasks -->
        <el-card class="box-card mt-20">
          <template #header>
            <div class="card-header">
              <span>Recent Tasks</span>
              <el-button text @click="router.push('/tasks')">View All</el-button>
            </div>
          </template>
          <div v-if="tasks.length === 0" class="empty-text">No tasks found.</div>
          <div v-for="task in tasks.slice(0, 5)" :key="task.id" class="text item task-item">
            <div class="task-info">
              <span :class="{ 'completed': task.status === 'DONE' }">{{ task.title }}</span>
              <span v-if="task.dueDate" class="task-due">{{ formatDateShort(task.dueDate) }}</span>
            </div>
            <el-tag size="small" :type="getStatusType(task.status)">{{ task.status }}</el-tag>
          </div>
        </el-card>
      </el-col>

      <!-- Right Column: Stats & Quick Actions -->
      <el-col :span="8">
        <!-- Streak Card -->
        <el-card class="box-card mb-20 streak-card" v-if="streakDays > 0">
          <div class="streak-content">
            <div class="streak-icon-large">
              <el-icon><Opportunity /></el-icon>
            </div>
            <div class="streak-text">
              <span class="streak-number">{{ streakDays }}</span>
              <span class="streak-label">Day Streak!</span>
            </div>
          </div>
        </el-card>

        <!-- Pomodoro Stats -->
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>Today's Focus</span>
            </div>
          </template>
          <div class="stat-item">
            <h3>Completed Pomodoros</h3>
            <p class="stat-number">{{ pomodoroStore.completedPomodoros }}</p>
            <p class="stat-subtitle">Approx. {{ pomodoroStore.completedPomodoros * 25 }} minutes</p>
          </div>
          <div class="action-buttons">
            <el-button type="primary" class="w-100" @click="router.push('/pomodoro')">Start Focus</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getTasks } from '@/api/task'
import { getDailyFocusStats } from '@/api/stats'
import { getUserStats } from '@/api/gamification'
import { useUserStore } from '@/stores/user'
import { usePomodoroStore } from '@/stores/pomodoro'
import { Opportunity } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const pomodoroStore = usePomodoroStore()
const tasks = ref([])
const dailyFocusData = ref([])
const calendarDate = ref(new Date())
const streakDays = ref(0)

const currentDate = computed(() => {
  return new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })
})

const fetchTasks = async () => {
  if (!userStore.user || !userStore.user.id) return
  try {
    const data = await getTasks(userStore.user.id)
    tasks.value = data || []
  } catch (error) {
    console.error(error)
  }
}

const fetchFocusStats = async () => {
  if (!userStore.user || !userStore.user.id) return
  try {
    const data = await getDailyFocusStats(userStore.user.id)
    dailyFocusData.value = data || []
  } catch (error) {
    console.error(error)
  }
}

const fetchUserStreak = async () => {
  if (!userStore.user || !userStore.user.id) return
  try {
    const stats = await getUserStats(userStore.user.id)
    streakDays.value = stats.streakDays || 0
  } catch (error) {
    console.error(error)
  }
}

const getTasksForDate = (dateStr) => {
  return tasks.value.filter(task => {
    if (!task.dueDate) return false
    const taskDate = new Date(task.dueDate).toISOString().split('T')[0]
    return taskDate === dateStr
  })
}

const getFocusTimeForDate = (dateStr) => {
  const stat = dailyFocusData.value.find(d => d.date === dateStr)
  return stat ? stat.totalSeconds : 0
}

const isStreakDay = (dateStr) => {
  // Simple visualization: Mark days with focus time as part of streak history
  // For a true "streak" highlight, we'd need more complex backend logic returning specific streak dates
  // For now, let's just show the flame if there was focus time > 0
  return getFocusTimeForDate(dateStr) > 0
}

const getStatusType = (status) => {
  const map = {
    'DONE': 'success',
    'IN_PROGRESS': 'primary',
    'TODO': 'info'
  }
  return map[status] || ''
}

const formatDateShort = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth()+1}/${date.getDate()}`
}

onMounted(() => {
  fetchTasks()
  fetchFocusStats()
  fetchUserStreak()
  pomodoroStore.fetchTodayCount()
})
</script>

<style scoped>
.dashboard-container {
  padding-bottom: 20px;
}
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.date-display {
  color: #909399;
  font-size: 1.1rem;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}
.task-item:last-child {
  border-bottom: none;
}
.task-info {
  display: flex;
  flex-direction: column;
}
.task-due {
  font-size: 12px;
  color: #909399;
}
.completed {
  text-decoration: line-through;
  color: #999;
}
.empty-text {
  color: #909399;
  text-align: center;
  padding: 20px 0;
}
.stat-item {
  text-align: center;
  padding: 20px 0;
}
.stat-number {
  font-size: 48px;
  font-weight: bold;
  color: #409eff;
  margin: 10px 0;
}
.stat-subtitle {
  color: #909399;
  font-size: 14px;
}
.action-buttons {
  margin-top: 20px;
}
.w-100 {
  width: 100%;
}
.mt-20 {
  margin-top: 20px;
}
.mb-20 {
  margin-bottom: 20px;
}

/* Calendar Styles */
.calendar-card :deep(.el-calendar-table .el-calendar-day) {
  height: 80px;
  padding: 5px;
}
.calendar-cell {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.day-number {
  font-weight: bold;
}
.day-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
}
.task-dot {
  width: 6px;
  height: 6px;
  background-color: #f56c6c;
  border-radius: 50%;
  display: inline-block;
}
.focus-badge {
  background-color: #e1f3d8;
  color: #67c23a;
  font-size: 10px;
  padding: 2px 4px;
  border-radius: 4px;
}
.streak-icon {
  margin-top: 2px;
}

/* Streak Card Styles */
.streak-card {
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 99%, #fad0c4 100%);
  color: white;
  border: none;
}
.streak-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}
.streak-icon-large {
  font-size: 40px;
  animation: flame 1.5s infinite ease-in-out;
}
.streak-text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.streak-number {
  font-size: 32px;
  font-weight: bold;
  line-height: 1;
}
.streak-label {
  font-size: 14px;
  opacity: 0.9;
}

@keyframes flame {
  0% { transform: scale(1); opacity: 0.8; }
  50% { transform: scale(1.1); opacity: 1; }
  100% { transform: scale(1); opacity: 0.8; }
}
</style>
