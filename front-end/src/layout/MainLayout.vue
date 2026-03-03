<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical-demo"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>Dashboard</span>
        </el-menu-item>
        <el-menu-item index="/tasks">
          <el-icon><List /></el-icon>
          <span>Tasks</span>
        </el-menu-item>
        <el-menu-item index="/pomodoro">
          <el-icon><Timer /></el-icon>
          <span>Pomodoro</span>
        </el-menu-item>
        <el-menu-item index="/stats">
          <el-icon><TrendCharts /></el-icon>
          <span>Statistics</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-content">
          <h2>A Clock Inside the Rose</h2>
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ username }}
              <el-icon class="el-icon--right">
                <arrow-down />
              </el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">Profile</el-dropdown-item>
                <el-dropdown-item divided command="logout">Logout</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const username = computed(() => userStore.user?.username || 'User')

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.el-aside {
  background-color: #f5f7fa;
  border-right: 1px solid #e6e6e6;
}
.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
}
</style>
