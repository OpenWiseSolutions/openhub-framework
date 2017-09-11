import apiService from './api.service'

export const URL = {
  catalogs: '/api/catalogs'
}

export const fetchCatalog = (name) => apiService.get(`${URL.catalogs}/${name}`)
