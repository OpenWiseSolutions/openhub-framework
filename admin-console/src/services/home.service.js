import apiService from './api.service'

export const URL = {
  getInfo: '/mgmt/info',
  getHealth: '/mgmt/health',
  getMetrics: '/mgmt/ohfmetrics'
}

export const fetchInfo = () => apiService.get(URL.getInfo)
export const fetchHealth = () => apiService.get(URL.getHealth)
export const fetchMetrics = () => apiService.get(URL.getMetrics)
