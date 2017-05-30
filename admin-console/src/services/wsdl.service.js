import apiService from './api.service'

export const URL = {
  get: '/api/services/wsdl'
}

export const fetchWsdl = () => apiService.get(URL.get)

