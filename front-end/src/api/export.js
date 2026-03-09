import request from '@/utils/request'

export function exportTasks(userId) {
  return request({
    url: '/export/tasks',
    method: 'get',
    params: { userId },
    responseType: 'blob'
  })
}

export function exportPomodoro(userId) {
  return request({
    url: '/export/pomodoro',
    method: 'get',
    params: { userId },
    responseType: 'blob'
  })
}
