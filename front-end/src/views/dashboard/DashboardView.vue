<template>
  <div>
    <h1>Dashboard</h1>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>My Tasks</span>
              <el-button text @click="router.push('/tasks')">View All</el-button>
            </div>
          </template>
          <div v-if="tasks.length === 0" class="empty-text">No tasks found.</div>
          <div v-for="task in tasks.slice(0, 5)" :key="task.id" class="text item task-item">
            <span :class="{ 'completed': task.status === 'DONE' }">{{ task.title }}</span>
            <el-tag size="small" :type="getStatusType(task.status)">{{ task.status }}</el-tag>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>Pomodoro Stats</span>
            </div>
          </template>
          <div class="stat-item">
            <h3>Completed Pomodoros</h3>
            <p class="stat-number">{{ pomodoroStore.completedPomodoros }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTasks } from '@/api/task'
import { useUserStore } from '@/stores/user'
import { usePomodoroStore } from '@/stores/pomodoro'

const router = useRouter()
const userStore = useUserStore()
const pomodoroStore = usePomodoroStore()
const tasks = ref([])

const fetchTasks = async () => {
  if (!userStore.user || !userStore.user.id) return
  try {
    const data = await getTasks(userStore.user.id)
    tasks.value = data || []
  } catch (error) {
    console.error(error)
  }
}

const getStatusType = (status) => {
  const map = {
    'DONE': 'success',
    'IN_PROGRESS': 'primary',
    'TODO': 'info'
  }
  return map[status] || ''
}

onMounted(() => {
  fetchTasks()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}
.task-item:last-child {
  border-bottom: none;
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
  padding: 20px;
}
.stat-number {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
  margin: 10px 0;
}
</style>
