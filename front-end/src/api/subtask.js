import request from '@/utils/request'

export function createSubtask(data) {
  return request({
    url: '/subtasks',
    method: 'post',
    data
  })
}

export function updateSubtask(id, data) {
  return request({
    url: `/subtasks/${id}`,
    method: 'put',
    data
  })
}

export function deleteSubtask(id) {
  return request({
    url: `/subtasks/${id}`,
    method: 'delete'
  })
}
