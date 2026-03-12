<template>
  <div class="dashboard-view" ref="dashboardRef">
    <TimerSection @scroll-down="scrollToWidgets" />
    <DashboardWidgets ref="widgetsRef" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import TimerSection from './TimerSection.vue'
import DashboardWidgets from './DashboardWidgets.vue'

const dashboardRef = ref(null)
const widgetsRef = ref(null)

const scrollToWidgets = () => {
  if (widgetsRef.value && widgetsRef.value.$el) {
    widgetsRef.value.$el.scrollIntoView({ behavior: 'smooth' })
  }
}
</script>

<style scoped>
.dashboard-view {
  width: 100%;
}
</style>
