<template>
  <div class="settings-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>Edit Profile</span>
        </div>
      </template>
      
      <el-form :model="form" label-width="120px" v-loading="loading">
        <el-form-item label="Avatar URL">
          <el-input v-model="form.avatarUrl" placeholder="https://example.com/avatar.png" />
          <div class="avatar-preview" v-if="form.avatarUrl">
            <el-avatar :size="50" :src="form.avatarUrl" />
          </div>
        </el-form-item>

        <el-form-item label="Username">
          <el-input v-model="form.username" disabled />
          <span class="hint">Username cannot be changed</span>
        </el-form-item>

        <el-form-item label="Nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>

        <el-form-item label="Bio">
          <el-input v-model="form.bio" type="textarea" :rows="3" />
        </el-form-item>

        <el-form-item label="Gender">
          <el-radio-group v-model="form.gender">
            <el-radio label="MALE">Male</el-radio>
            <el-radio label="FEMALE">Female</el-radio>
            <el-radio label="OTHER">Other</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="Website">
          <el-input v-model="form.website" placeholder="https://your-site.com" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="saveProfile">Save Changes</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useUserStore } from '@/stores/user'
import { getUserProfile, updateUserProfile } from '@/api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const loading = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  avatarUrl: '',
  bio: '',
  gender: '',
  website: ''
})

const fetchProfile = async () => {
  if (!userStore.user || !userStore.user.id) return
  loading.value = true
  try {
    const res = await getUserProfile(userStore.user.id)
    if (res) {
      Object.assign(form, res)
    }
  } catch (e) {
    ElMessage.error('Failed to load profile')
  } finally {
    loading.value = false
  }
}

const saveProfile = async () => {
  if (!userStore.user || !userStore.user.id) return
  try {
    await updateUserProfile(userStore.user.id, form)
    ElMessage.success('Profile updated successfully')
    // Update local user store nickname if needed
    // userStore.updateUser({ ...userStore.user, nickname: form.nickname })
  } catch (e) {
    ElMessage.error('Failed to update profile')
  }
}

onMounted(() => {
  fetchProfile()
})
</script>

<style scoped>
.settings-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}
.hint {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
}
.avatar-preview {
  margin-top: 10px;
}
</style>
