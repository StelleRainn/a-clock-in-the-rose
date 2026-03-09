<template>
  <div class="task-list-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>Task Management</span>
          <el-button type="primary" @click="openCreateDialog">Add Task</el-button>
        </div>
      </template>

      <!-- Filter & Sort Bar -->
      <div class="filter-bar">
        <el-input
          v-model="searchQuery"
          placeholder="Search task title..."
          prefix-icon="Search"
          style="width: 250px"
          clearable
        />
        
        <el-select 
          v-model="selectedFilterTags" 
          multiple 
          collapse-tags
          placeholder="Filter by Tags" 
          style="width: 200px"
          clearable
        >
          <el-option
            v-for="tag in availableTags"
            :key="tag.id"
            :label="tag.name"
            :value="tag.id"
          >
            <span :style="{ color: tag.color }">●</span> {{ tag.name }}
          </el-option>
        </el-select>

        <el-select v-model="sortBy" placeholder="Sort by" style="width: 150px">
          <el-option label="Created Date" value="createdAt" />
          <el-option label="Due Date" value="dueDate" />
          <el-option label="Priority" value="priority" />
          <el-option label="Status" value="status" />
        </el-select>

        <el-button @click="toggleSortOrder" :icon="sortOrder === 'asc' ? 'SortUp' : 'SortDown'">
          {{ sortOrder === 'asc' ? 'Asc' : 'Desc' }}
        </el-button>
      </div>

      <el-tabs v-model="activeName">
        <el-tab-pane label="List View" name="list">
          <el-table :data="filteredTasks" style="width: 100%" v-loading="loading" row-key="id">
            <el-table-column type="expand">
              <template #default="props">
                <div class="subtask-container">
                  <div class="subtask-header">
                    <h4>Subtasks ({{ props.row.subtasks ? props.row.subtasks.length : 0 }})</h4>
                    <el-button size="small" type="primary" link @click="openAddSubtask(props.row)">+ Add Subtask</el-button>
                  </div>
                  <div v-if="!props.row.subtasks || props.row.subtasks.length === 0" class="no-subtasks">
                    No subtasks yet.
                  </div>
                  <ul v-else class="subtask-list">
                    <li v-for="sub in props.row.subtasks" :key="sub.id" class="subtask-item">
                      <el-checkbox 
                        v-model="sub.completed" 
                        @change="(val) => handleSubtaskStatusChange(sub, val)"
                      >
                        <span :class="{ 'completed-text': sub.completed }">{{ sub.title }}</span>
                      </el-checkbox>
                      <el-button 
                        type="danger" 
                        icon="Delete" 
                        circle 
                        size="small" 
                        link 
                        @click="handleDeleteSubtask(sub.id, props.row)"
                      />
                    </li>
                  </ul>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="title" label="Task Name" />
            <el-table-column label="Tags" width="200">
              <template #default="scope">
                <el-tag 
                  v-for="tag in scope.row.tags" 
                  :key="tag.id" 
                  :color="tag.color" 
                  effect="dark"
                  size="small"
                  class="mr-5"
                  style="border: none;"
                >
                  {{ tag.name }}
                </el-tag>
              </template>
            </el-table-column>
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
                    
                    <!-- Subtask Progress in Kanban -->
                    <div class="subtask-progress" v-if="element.subtasks && element.subtasks.length > 0">
                      <el-progress 
                        :percentage="calculateProgress(element.subtasks)" 
                        :show-text="false" 
                        :stroke-width="4"
                        :color="calculateProgress(element.subtasks) === 100 ? '#67c23a' : '#409eff'"
                      />
                      <span class="subtask-count">
                        {{ element.subtasks.filter(s => s.completed).length }}/{{ element.subtasks.length }}
                      </span>
                    </div>

                    <div class="card-tags" v-if="element.tags && element.tags.length">
                      <el-tag 
                        v-for="tag in element.tags" 
                        :key="tag.id" 
                        :color="tag.color" 
                        effect="dark"
                        size="small"
                        class="mr-5"
                        style="border: none; font-size: 10px; height: 18px; padding: 0 5px;"
                      >
                        {{ tag.name }}
                      </el-tag>
                    </div>
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
        
        <!-- Tag Selection -->
        <el-form-item label="Tags">
          <el-select
            v-model="selectedTagIds"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="Select or create tags"
            @change="handleTagChange"
          >
            <el-option
              v-for="tag in availableTags"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            >
              <span :style="{ color: tag.color }">●</span> {{ tag.name }}
            </el-option>
          </el-select>
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

    <!-- Subtask Add Dialog -->
    <el-dialog v-model="subtaskDialogVisible" title="Add Subtask" width="30%">
      <el-input v-model="newSubtaskTitle" placeholder="Enter subtask title" @keyup.enter="submitSubtask" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="subtaskDialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="submitSubtask">Add</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getTasks, createTask, updateTask, deleteTask } from '@/api/task'
import { getTags, createTag } from '@/api/tag'
import { createSubtask, updateSubtask, deleteSubtask } from '@/api/subtask'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import draggable from 'vuedraggable'
import { Search, SortUp, SortDown, Delete } from '@element-plus/icons-vue'

const router = useRouter()
const activeName = ref('list')
const tableData = ref([])
const loading = ref(false)
const userStore = useUserStore()

const dialogVisible = ref(false)
const dialogTitle = ref('Create Task')
const isEditMode = ref(false)
const currentTaskId = ref(null)

// Subtask state
const subtaskDialogVisible = ref(false)
const newSubtaskTitle = ref('')
const currentParentTask = ref(null)

// Tags
const availableTags = ref([])
const selectedTagIds = ref([])

// Filters & Sort
const searchQuery = ref('')
const selectedFilterTags = ref([])
const sortBy = ref('createdAt')
const sortOrder = ref('desc') // asc or desc

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

const fetchTags = async () => {
  if (!userStore.user || !userStore.user.id) return
  try {
    const data = await getTags(userStore.user.id)
    availableTags.value = data || []
  } catch (error) {
    console.error('Failed to fetch tags', error)
  }
}

// Subtask Logic
const openAddSubtask = (task) => {
  currentParentTask.value = task
  newSubtaskTitle.value = ''
  subtaskDialogVisible.value = true
}

const submitSubtask = async () => {
  if (!newSubtaskTitle.value.trim()) return
  try {
    const res = await createSubtask({
      taskId: currentParentTask.value.id,
      title: newSubtaskTitle.value,
      completed: false
    })
    // Update local state
    if (!currentParentTask.value.subtasks) {
      currentParentTask.value.subtasks = []
    }
    currentParentTask.value.subtasks.push(res)
    ElMessage.success('Subtask added')
    subtaskDialogVisible.value = false
  } catch (e) {
    ElMessage.error('Failed to add subtask')
  }
}

const handleSubtaskStatusChange = async (sub, val) => {
  try {
    await updateSubtask(sub.id, { ...sub, completed: val })
  } catch (e) {
    ElMessage.error('Failed to update subtask')
    sub.completed = !val // Revert on error
  }
}

const handleDeleteSubtask = async (subId, parentTask) => {
  try {
    await deleteSubtask(subId)
    parentTask.subtasks = parentTask.subtasks.filter(s => s.id !== subId)
    ElMessage.success('Subtask deleted')
  } catch (e) {
    ElMessage.error('Failed to delete subtask')
  }
}

const calculateProgress = (subtasks) => {
  if (!subtasks || subtasks.length === 0) return 0
  const completed = subtasks.filter(s => s.completed).length
  return Math.round((completed / subtasks.length) * 100)
}

// Computed Filtered Tasks
const filteredTasks = computed(() => {
  let result = [...tableData.value]

  // 1. Filter by Title
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(task => task.title.toLowerCase().includes(query))
  }

  // 2. Filter by Tags
  if (selectedFilterTags.value.length > 0) {
    result = result.filter(task => {
      if (!task.tags) return false
      // Check if task has AT LEAST one of the selected tags (OR logic)
      // Or ALL? Usually OR is better for filtering. Let's use OR.
      // Actually, standard filtering often implies "has any of these tags"
      const taskTagIds = task.tags.map(t => t.id)
      return selectedFilterTags.value.some(id => taskTagIds.includes(id))
    })
  }

  // 3. Sort
  result.sort((a, b) => {
    let valA, valB
    
    switch (sortBy.value) {
      case 'priority':
        // Custom order: HIGH > MEDIUM > LOW
        const pMap = { 'HIGH': 3, 'MEDIUM': 2, 'LOW': 1 }
        valA = pMap[a.priority] || 0
        valB = pMap[b.priority] || 0
        break
      case 'status':
        // Custom order: TODO > IN_PROGRESS > DONE
        const sMap = { 'TODO': 1, 'IN_PROGRESS': 2, 'DONE': 3 }
        valA = sMap[a.status] || 0
        valB = sMap[b.status] || 0
        break
      case 'dueDate':
        valA = a.dueDate ? new Date(a.dueDate).getTime() : (sortOrder.value === 'asc' ? Infinity : -Infinity)
        valB = b.dueDate ? new Date(b.dueDate).getTime() : (sortOrder.value === 'asc' ? Infinity : -Infinity)
        break
      case 'createdAt':
      default:
        valA = a.createdAt ? new Date(a.createdAt).getTime() : 0
        valB = b.createdAt ? new Date(b.createdAt).getTime() : 0
    }

    if (valA < valB) return sortOrder.value === 'asc' ? -1 : 1
    if (valA > valB) return sortOrder.value === 'asc' ? 1 : -1
    return 0
  })

  return result
})

const toggleSortOrder = () => {
  sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
}

// ... (Rest of CRUD functions: handleTagChange, openCreateDialog, handleEdit, handleDelete, submitTask, etc. - Keep same)

const handleTagChange = async (val) => {
  // Check if new tag created
  const newTagNames = val.filter(v => typeof v === 'string')
  if (newTagNames.length > 0) {
    for (const name of newTagNames) {
      try {
        const newTag = await createTag({
          userId: userStore.user.id,
          name: name,
          color: getRandomColor()
        })
        availableTags.value.push(newTag)
        // Replace string ID with real ID in selection
        const index = selectedTagIds.value.indexOf(name)
        if (index !== -1) {
          selectedTagIds.value[index] = newTag.id
        }
      } catch (e) {
        ElMessage.error('Failed to create tag: ' + name)
      }
    }
  }
}

const getRandomColor = () => {
  const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#a0cfff', '#b3e19d']
  return colors[Math.floor(Math.random() * colors.length)]
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
  // Set selected tags
  selectedTagIds.value = row.tags ? row.tags.map(t => t.id) : []
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
  
  // Construct payload with tags
  const tagsPayload = selectedTagIds.value.map(id => ({ id }))
  
  const payload = {
    ...taskForm,
    userId: userStore.user.id,
    tags: tagsPayload
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
  selectedTagIds.value = []
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
// IMPORTANT: Kanban view should ALSO reflect filters? 
// The user request specifically mentioned "List View", but it's nice if Kanban also filters.
// Let's use filteredTasks for Kanban too!
const getTasksByStatus = (status) => {
  return filteredTasks.value.filter(task => task.status === status)
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
  fetchTags()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.filter-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
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
.card-tags {
  margin-bottom: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}
.mr-5 {
  margin-right: 5px;
}

/* Subtask Styles */
.subtask-container {
  padding: 10px 20px;
  background-color: #f9f9f9;
  border-radius: 4px;
}
.subtask-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.subtask-header h4 {
  margin: 0;
  font-size: 14px;
  color: #606266;
}
.subtask-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.subtask-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 5px 0;
  border-bottom: 1px dashed #e4e7ed;
}
.subtask-item:last-child {
  border-bottom: none;
}
.completed-text {
  text-decoration: line-through;
  color: #909399;
}
.no-subtasks {
  font-size: 12px;
  color: #909399;
  font-style: italic;
}

/* Kanban Subtask Progress */
.subtask-progress {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.subtask-progress .el-progress {
  flex: 1;
}
.subtask-count {
  font-size: 10px;
  color: #909399;
}
</style>
