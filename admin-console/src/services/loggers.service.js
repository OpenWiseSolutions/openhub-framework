import apiService from './api.service'

export const URL = {
  get: '/mgmt/loggers'
}

export const fetchLoggers = () => apiService.get(URL.get)
export const editLogger = (id, configuredLevel) => apiService.post(`${URL.get}/${id}`, { configuredLevel })
