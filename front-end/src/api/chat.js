import request from '@/utils/request'

export function getChatSessions(userId) {
  return request({
    url: '/chat/sessions',
    method: 'get',
    params: { userId }
  })
}

export function createChatSession(data) {
  return request({
    url: '/chat/sessions',
    method: 'post',
    data
  })
}

export function updateChatSessionTitle(id, title) {
  return request({
    url: `/chat/sessions/${id}`,
    method: 'put',
    data: { title }
  })
}

export function deleteChatSession(id) {
  return request({
    url: `/chat/sessions/${id}`,
    method: 'delete'
  })
}

export function getChatMessages(sessionId) {
  return request({
    url: `/chat/sessions/${sessionId}/messages`,
    method: 'get'
  })
}

export function saveChatMessage(sessionId, data) {
  return request({
    url: `/chat/sessions/${sessionId}/messages`,
    method: 'post',
    data
  })
}
