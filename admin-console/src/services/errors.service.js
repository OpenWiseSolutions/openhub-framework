import apiService from './api.service'

export const URL = {
  get: '/api/errors-catalog'
}

export const fetchErrorCatalog = () => apiService.get(URL.get)

