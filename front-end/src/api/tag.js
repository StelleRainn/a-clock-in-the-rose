import request from '@/utils/request'

export function getTags(userId) {
  return request({
    url: '/tags',
    method: 'get',
    params: { userId }
  })
}

export function createTag(data) {
  return request({
    url: '/tags',
    method: 'post',
    data
  })
}

export function deleteTag(id, userId) {
  return request({
    url: `/tags/${id}`,
    method: 'delete',
    params: { userId }
  })
}
