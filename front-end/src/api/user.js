import request from '@/utils/request'

export function getUserProfile(id) {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

export function updateUserProfile(id, data) {
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}
