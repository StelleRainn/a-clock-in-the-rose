<template>
  <nav class="mobile-nav-bar">
    <router-link to="/dashboard" class="nav-item" active-class="active">
      <div class="icon-container">
        <el-icon :size="24"><Odometer /></el-icon>
      </div>
      <span class="label">Focus</span>
    </router-link>
    
    <router-link to="/tasks" class="nav-item" active-class="active">
      <div class="icon-container">
        <el-icon :size="24"><List /></el-icon>
      </div>
      <span class="label">Tasks</span>
    </router-link>

    <div class="nav-item add-item" @click="$emit('add-task')">
      <div class="add-btn">
        <el-icon :size="24" color="white"><Plus /></el-icon>
      </div>
    </div>

    <router-link to="/stats" class="nav-item" active-class="active">
      <div class="icon-container">
        <el-icon :size="24"><TrendCharts /></el-icon>
      </div>
      <span class="label">Stats</span>
    </router-link>

    <router-link to="/intelligent" class="nav-item" active-class="active">
      <div class="icon-container">
        <el-icon :size="24"><Cpu /></el-icon>
      </div>
      <span class="label">AI</span>
    </router-link>
  </nav>
</template>

<script setup>
import { Odometer, List, Plus, TrendCharts, Cpu } from '@element-plus/icons-vue'
</script>

<style scoped>
.mobile-nav-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 60px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-around;
  align-items: center;
  z-index: 2000;
  padding-bottom: env(safe-area-inset-bottom);
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.05);
}

.dark .mobile-nav-bar {
  background: rgba(30, 30, 30, 0.85);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  color: var(--el-text-color-secondary);
  height: 100%;
  padding-top: 4px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.nav-item .icon-container {
  margin-bottom: 2px;
  transition: transform 0.3s;
}

.nav-item .label {
  font-size: 10px;
  font-weight: 500;
  opacity: 0.8;
}

.nav-item.active {
  color: var(--el-color-primary);
}

.nav-item.active .icon-container {
  transform: translateY(-2px);
}

.nav-item.active .label {
  opacity: 1;
  font-weight: 600;
}

/* Add Button Special Styling */
.add-item {
  position: relative;
  cursor: pointer;
}

.add-btn {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--el-color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(var(--el-color-primary-rgb), 0.4);
  transform: translateY(-12px);
  transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.add-btn:active {
  transform: translateY(-12px) scale(0.9);
}

/* Dark mode adjustments */
.dark .nav-item {
  color: #909399;
}
.dark .nav-item.active {
  color: var(--el-color-primary);
}
</style>
