import apiService from './api.service'

export const URL = {
  get: '/api/config-params',
  update: '/api/config-params'
}

export const fetchConfigParams = () => apiService.get(URL.get)
export const updateConfigParam = (code, payload) => apiService.put(`${URL.update}/${code}`, payload)
