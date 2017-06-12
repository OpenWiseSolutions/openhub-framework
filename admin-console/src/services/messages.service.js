import apiService from './api.service'

export const URL = {
  get: '/api/messages'
}

export const fetchMessages = (filter) => apiService.get(URL.get, filter)
export const fetchMessage = (id) => apiService.get(`${URL.get}/${id}`)
export const restartMessage = (id, totalRestart = false) => apiService
  .post(`${URL.get}/${id}/action`, { type: 'RESTART', data: { totalRestart } })
export const cancelMessage = (id) => apiService
  .post(`${URL.get}/${id}/action`, { type: 'CANCEL' })
