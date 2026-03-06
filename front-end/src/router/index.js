import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layout/MainLayout.vue'
import LoginView from '../views/auth/LoginView.vue'
import DashboardView from '../views/dashboard/DashboardView.vue'
import TaskListView from '../views/tasks/TaskListView.vue'
import PomodoroTimer from '../views/pomodoro/PomodoroTimer.vue'
import StatsOverview from '../views/stats/StatsOverview.vue'
import ProfileSettings from '../views/settings/ProfileSettings.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/',
      component: MainLayout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: DashboardView
        },
        {
          path: 'tasks',
          name: 'tasks',
          component: TaskListView
        },
        {
          path: 'pomodoro',
          name: 'pomodoro',
          component: PomodoroTimer
        },
        {
          path: 'stats',
          name: 'stats',
          component: StatsOverview
        },
        {
          path: 'profile',
          name: 'profile',
          component: ProfileSettings
        }
      ]
    }
  ],
})

export default router
