<template>
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getTasks } from '@/api/task'
import { getDailyFocusStats } from '@/api/stats'
import { getUserStats } from '@/api/gamification'
import { Opportunity } from '@element-plus/icons-vue'

const userStore = useUserStore()
const calendarDate = ref(new Date())
const tasks = ref([])
const dailyFocusData = ref([])
const streakDays = ref(0)

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
    console.error('Failed to fetch heatmap data', e)
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

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 1.1rem;
}

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
</style>
