import apiService from './api.service'

export const URL = {
  get: '/api/cluster/nodes'
}

export const fetchNodes = () => apiService.get(URL.get)
export const editNode = (id, payload) => apiService.put(`${URL.get}/${id}`, payload)
export const removeNode = (id) => apiService.remove(`${URL.get}/${id}`)
