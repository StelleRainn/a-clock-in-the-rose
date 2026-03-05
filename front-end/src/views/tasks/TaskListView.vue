<template>
  <div class="task-list-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>Task Management</span>
          <el-button type="primary" @click="openCreateDialog">Add Task</el-button>
        </div>
      </template>
      <el-tabs v-model="activeName">
        <el-tab-pane label="List View" name="list">
          <el-table :data="tableData" style="width: 100%" v-loading="loading">
            <el-table-column prop="title" label="Task Name" />
            <el-table-column prop="priority" label="Priority" width="120">
              <template #default="scope">
                <el-tag :type="getPriorityType(scope.row.priority)">{{ scope.row.priority }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="Status" width="120">
               <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="dueDate" label="Due Date" width="180">
               <template #default="scope">
                 {{ formatDate(scope.row.dueDate) }}
               </template>
            </el-table-column>
            <el-table-column label="Actions" width="200">
              <template #default="scope">
                <el-button size="small" @click="handleEdit(scope.row)">Edit</el-button>
                <el-button size="small" type="danger" @click="handleDelete(scope.row)">Delete</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="Kanban View" name="kanban">
          <div class="kanban-board">
            <div class="kanban-column" v-for="status in ['TODO', 'IN_PROGRESS', 'DONE']" :key="status">
              <div class="column-header">
                <h3>{{ formatStatus(status) }}</h3>
                <el-tag :type="getStatusType(status)">{{ getTasksByStatus(status).length }}</el-tag>
              </div>
              <draggable 
                :list="getTasksByStatus(status)" 
                group="tasks" 
                item-key="id"
                @change="(evt) => handleDragChange(evt, status)"
                class="draggable-area"
              >
                <template #item="{ element }">
                  <div class="kanban-card" @click="handleEdit(element)">
                    <div class="card-title">{{ element.title }}</div>
                    <div class="card-meta">
                      <el-tag size="small" :type="getPriorityType(element.priority)">{{ element.priority }}</el-tag>
                      <span class="card-date" v-if="element.dueDate">{{ formatDateShort(element.dueDate) }}</span>
                    </div>
                  </div>
                </template>
              </draggable>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- Task Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form :model="taskForm" label-width="100px">
        <el-form-item label="Title">
          <el-input v-model="taskForm.title" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="taskForm.description" type="textarea" />
        </el-form-item>
        <el-form-item label="Priority">
          <el-select v-model="taskForm.priority">
            <el-option label="Low" value="LOW" />
            <el-option label="Medium" value="MEDIUM" />
            <el-option label="High" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="Status">
          <el-select v-model="taskForm.status">
            <el-option label="To Do" value="TODO" />
            <el-option label="In Progress" value="IN_PROGRESS" />
            <el-option label="Done" value="DONE" />
          </el-select>
        </el-form-item>
        <el-form-item label="Due Date">
          <el-date-picker
            v-model="taskForm.dueDate"
            type="datetime"
            placeholder="Select date and time"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="submitTask">Confirm</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getTasks, createTask, updateTask, deleteTask } from '@/api/task'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import draggable from 'vuedraggable'

const router = useRouter()
const activeName = ref('list')
const tableData = ref([])
const loading = ref(false)
const userStore = useUserStore()

const dialogVisible = ref(false)
const dialogTitle = ref('Create Task')
const isEditMode = ref(false)
const currentTaskId = ref(null)

const taskForm = reactive({
  title: '',
  description: '',
  priority: 'MEDIUM',
  status: 'TODO',
  dueDate: ''
})

const fetchTasks = async () => {
  if (!userStore.user || !userStore.user.id) return
  loading.value = true
  try {
    const data = await getTasks(userStore.user.id)
    tableData.value = data || []
  } catch (error) {
    ElMessage.error('Failed to fetch tasks')
  } finally {
    loading.value = false
  }
}

const openCreateDialog = () => {
  if (!userStore.user) {
    ElMessage.warning('Please login first')
    router.push('/login')
    return
  }
  isEditMode.value = false
  dialogTitle.value = 'Create Task'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEditMode.value = true
  dialogTitle.value = 'Edit Task'
  currentTaskId.value = row.id
  taskForm.title = row.title
  taskForm.description = row.description
  taskForm.priority = row.priority
  taskForm.status = row.status
  taskForm.dueDate = row.dueDate
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    'Are you sure to delete this task?',
    'Warning',
    {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        await deleteTask(row.id)
        ElMessage.success('Delete completed')
        fetchTasks()
      } catch (e) {
        ElMessage.error('Delete failed')
      }
    })
    .catch(() => {})
}

const submitTask = async () => {
  if (!userStore.user || !userStore.user.id) {
    ElMessage.error('User not logged in')
    router.push('/login')
    return
  }

  if (!taskForm.title) {
    ElMessage.warning('Title is required')
    return
  }
  
  const payload = {
    ...taskForm,
    userId: userStore.user.id
  }

  try {
    if (isEditMode.value) {
      await updateTask(currentTaskId.value, payload)
      ElMessage.success('Task updated')
    } else {
      await createTask(payload)
      ElMessage.success('Task created')
    }
    dialogVisible.value = false
    fetchTasks()
  } catch (e) {
    ElMessage.error('Operation failed')
  }
}

const resetForm = () => {
  taskForm.title = ''
  taskForm.description = ''
  taskForm.priority = 'MEDIUM'
  taskForm.status = 'TODO'
  taskForm.dueDate = ''
}

const getPriorityType = (priority) => {
  const map = {
    'HIGH': 'danger',
    'MEDIUM': 'warning',
    'LOW': 'info'
  }
  return map[priority] || ''
}

const getStatusType = (status) => {
  const map = {
    'DONE': 'success',
    'IN_PROGRESS': 'primary',
    'TODO': 'info'
  }
  return map[status] || ''
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString()
}

const formatDateShort = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth()+1}/${date.getDate()}`
}

const formatStatus = (status) => {
  const map = {
    'TODO': 'To Do',
    'IN_PROGRESS': 'In Progress',
    'DONE': 'Done'
  }
  return map[status] || status
}

// Kanban Helpers
const getTasksByStatus = (status) => {
  return tableData.value.filter(task => task.status === status)
}

const handleDragChange = async (evt, newStatus) => {
  if (evt.added) {
    const task = evt.added.element
    // Optimistic update locally first
    task.status = newStatus
    
    // Call API to update status
    try {
      await updateTask(task.id, { ...task, status: newStatus, userId: userStore.user.id })
      // No need to fetchTasks() if optimistic update works, but for safety we can
      // fetchTasks() 
    } catch (e) {
      ElMessage.error('Failed to update task status')
      fetchTasks() // Revert on error
    }
  }
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
.kanban-board {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  padding-bottom: 20px;
  min-height: 400px;
}
.kanban-column {
  flex: 1;
  min-width: 250px;
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 10px;
  display: flex;
  flex-direction: column;
}
.column-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding: 0 5px;
}
.column-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}
.draggable-area {
  flex: 1;
  min-height: 100px;
}
.kanban-card {
  background-color: white;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.kanban-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
.card-title {
  font-weight: 500;
  margin-bottom: 8px;
  color: #303133;
}
.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-date {
  font-size: 12px;
  color: #909399;
}
</style>
