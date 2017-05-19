import apiService from './api.service'

export const URL = {
  getData: '/mgmt/env',
  getConfig: '/mgmt/configprops'
}

export const fetchEnvironmentData = () => apiService.get(URL.getData)
export const fetchEnvironmentConfig = () => apiService.get(URL.getConfig)

