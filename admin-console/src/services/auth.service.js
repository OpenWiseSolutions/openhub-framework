import apiService from './api.service'

export const URL = {
  login: '/login',
  logout: '/logout',
  userInfo: '/api/auth'
}

// ------------------------------------
// AUTH API
// ------------------------------------

// "special" login fetch
const loginConfig = (body) =>
  apiService.config({
    method: 'POST',
    body,
    headers: { 'content-type': 'application/x-www-form-urlencoded' }
  })

export const login = (body) =>
  apiService.http(apiService.getPath(URL.login), loginConfig(body))

// classic fetch
export const logout = () => apiService.get(URL.logout)
export const userInfo = () => apiService.get(URL.userInfo)
