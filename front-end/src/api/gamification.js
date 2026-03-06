import request from '@/utils/request'

export function getUserStats(userId) {
  return request({
    url: '/gamification/stats',
    method: 'get',
    params: { userId }
  })
}

export function getUserAchievements(userId) {
  return request({
    url: '/gamification/achievements',
    method: 'get',
    params: { userId }
  })
}
