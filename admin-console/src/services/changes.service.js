import apiService from './api.service'

const config = {
  headers: { Accept: 'text/plain' }
}

export const URL = {
  get: '/changes'
}

export const fetchChanges = () => apiService.get(URL.get, null, config)
