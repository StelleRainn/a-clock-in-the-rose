<template>
  <div class="dashboard-widgets">
    <el-row :gutter="24">
      <!-- Calendar / Heatmap Widget -->
      <el-col :span="14" :xs="24">
        <el-card class="widget-card glass-card">
          <template #header>
            <div class="card-header">
              <span>Activity Heatmap</span>
              <span class="streak-badge" v-if="streakDays > 0">
                <el-icon color="#f56c6c"><Opportunity /></el-icon> {{ streakDays }} Day Streak
              </span>
            </div>
          </template>
          <el-calendar v-model="calendarDate">
            <template #date-cell="{ data }">
              <div class="calendar-cell" :class="{ 'has-focus': getFocusTimeForDate(data.day) > 0 }">
                <span class="day-num">{{ data.day.split('-').slice(2).join('') }}</span>
                <div class="indicators">
                  <span v-if="getTasksForDate(data.day).length > 0" class="dot task-dot"></span>
                  <span v-if="getFocusTimeForDate(data.day) > 0" class="dot focus-dot"></span>
                </div>
              </div>
            </template>
          </el-calendar>
        </el-card>
      </el-col>

      <!-- Right Column -->
      <el-col :span="10" :xs="24">
        <!-- Daily Focus Stats -->
        <el-card class="widget-card glass-card mb-4">
          <div class="focus-stat-container">
            <div class="ring-chart">
              <el-progress type="dashboard" :percentage="dailyGoalPercentage" :color="colors">
                <template #default="{ percentage }">
                  <span class="percentage-value">{{ Math.round(pomodoroStore.todayFocusSeconds / 60) }}m</span>
                  <span class="percentage-label">Focused</span>
                </template>
              </el-progress>
            </div>
            <div class="stat-details">
              <h3>Today's Focus</h3>
              <p>{{ pomodoroStore.completedPomodoros }} Pomodoros completed</p>
            </div>
          </div>
        </el-card>

        <!-- Recent Tasks -->
        <el-card class="widget-card glass-card">
          <template #header>
            <div class="card-header">
              <span>Upcoming Tasks</span>
              <el-button link @click="$router.push('/tasks')">View All</el-button>
            </div>
          </template>
          <div class="task-list">
            <div v-for="task in recentTasks" :key="task.id" class="task-item">
              <el-checkbox v-model="task.done" @change="toggleTask(task)" />
              <span class="task-title" :class="{ done: task.status === 'DONE' }">{{ task.title }}</span>
              <el-tag size="small" :type="getPriorityType(task.priority)">{{ task.priority }}</el-tag>
            </div>
            <div v-if="recentTasks.length === 0" class="empty-state">
              No pending tasks. <el-button link type="primary" @click="$emit('add-task')">Add one?</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePomodoroStore } from '@/stores/pomodoro'
import { getTasks, updateTask } from '@/api/task'
import { getDailyFocusStats } from '@/api/stats'
import { getUserStats } from '@/api/gamification'
import { Opportunity } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const pomodoroStore = usePomodoroStore()

const calendarDate = ref(new Date())
const tasks = ref([])
const dailyFocusData = ref([])
const streakDays = ref(0)
const dailyGoalMinutes = 120 // Example goal

const colors = [
  { color: '#f56c6c', percentage: 20 },
  { color: '#e6a23c', percentage: 40 },
  { color: '#5cb87a', percentage: 60 },
  { color: '#1989fa', percentage: 80 },
  { color: '#6f7ad3', percentage: 100 },
]

const dailyGoalPercentage = computed(() => {
  const minutes = pomodoroStore.todayFocusSeconds / 60
  return Math.min((minutes / dailyGoalMinutes) * 100, 100)
})

const recentTasks = computed(() => {
  return tasks.value
    .filter(t => t.status !== 'DONE')
    .slice(0, 5)
})

const fetchData = async () => {
  if (!userStore.user?.id) return
  try {
    const [tasksRes, focusRes, statsRes] = await Promise.all([
      getTasks(userStore.user.id),
      getDailyFocusStats(userStore.user.id),
      getUserStats(userStore.user.id)
    ])
    tasks.value = tasksRes || []
    dailyFocusData.value = focusRes || []
    streakDays.value = statsRes?.streakDays || 0
  } catch (e) {
    console.error('Failed to fetch dashboard data', e)
  }
}

const getFocusTimeForDate = (dateStr) => {
  const stat = dailyFocusData.value.find(d => d.date === dateStr)
  return stat ? stat.totalSeconds : 0
}

const getTasksForDate = (dateStr) => {
  return tasks.value.filter(task => {
    if (!task.dueDate) return false
    const taskDate = new Date(task.dueDate).toISOString().split('T')[0]
    return taskDate === dateStr
  })
}

const toggleTask = async (task) => {
  // task.done is v-model, but we need to update status
  const newStatus = task.status === 'DONE' ? 'TODO' : 'DONE'
  // Optimistic update
  task.status = newStatus
  
  try {
    await updateTask(task.id, { ...task, status: newStatus, userId: userStore.user.id })
    ElMessage.success('Task updated')
  } catch (e) {
    // Revert
    task.status = newStatus === 'DONE' ? 'TODO' : 'DONE'
    ElMessage.error('Failed to update task')
  }
}

const getPriorityType = (p) => {
  const map = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' }
  return map[p] || ''
}

onMounted(() => {
  fetchData()
  pomodoroStore.fetchTodayCount()
})
</script>

<style scoped>
.dashboard-widgets {
  padding: 40px 20px;
  min-height: 100vh;
  background: rgba(255, 255, 255, 0.5); /* Slight tint for contrast below fold */
  backdrop-filter: blur(20px);
}

.dark .dashboard-widgets {
  background: rgba(0, 0, 0, 0.5);
}

.widget-card {
  border: none;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.07);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.dark .widget-card {
  background: rgba(30, 30, 30, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.widget-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.15);
}

.mb-4 {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 1.1rem;
}

/* Calendar Tweaks */
.calendar-cell {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.has-focus {
  background-color: rgba(var(--el-color-primary-rgb), 0.1);
  border-radius: 4px;
}

.indicators {
  display: flex;
  gap: 4px;
  margin-top: 4px;
}

.dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
}

.task-dot { background-color: var(--el-color-warning); }
.focus-dot { background-color: var(--el-color-success); }

/* Stats Widget */
.focus-stat-container {
  display: flex;
  align-items: center;
  gap: 24px;
}

.percentage-value {
  display: block;
  font-size: 24px;
  font-weight: bold;
}

.percentage-label {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.stat-details h3 {
  margin: 0 0 8px 0;
  font-size: 1.2rem;
}

.stat-details p {
  margin: 0;
  color: var(--el-text-color-secondary);
}

/* Task List */
.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.3);
  transition: background 0.2s;
}

.dark .task-item {
  background: rgba(255, 255, 255, 0.05);
}

.task-item:hover {
  background: rgba(255, 255, 255, 0.5);
}

.task-title {
  flex: 1;
  font-weight: 500;
}

.task-title.done {
  text-decoration: line-through;
  opacity: 0.6;
}

.empty-state {
  text-align: center;
  color: var(--el-text-color-secondary);
  padding: 20px;
}
</style>
