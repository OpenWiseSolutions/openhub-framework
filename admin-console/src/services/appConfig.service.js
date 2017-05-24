import apiService from './api.service'

export const URL = {
  consoleConfig: {
    path: '/api/console-config'
  }
}

// ------------------------------------
// AUTH API
// ------------------------------------
export const fetchConsoleConfig = () => apiService.get(URL.consoleConfig.path)
