import apiService from './api.service'

export const URL = {
  get: '/config-params',
  update: '/config-params'
}

export const fetchConfigParams = () => apiService.get(URL.get)
export const updateConfigParam = (code, payload) => apiService.put(`${URL.update}/${code}`, payload)
