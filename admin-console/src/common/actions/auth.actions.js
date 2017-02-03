import axios from 'axios'
export const INIT_AUTH = 'INIT_AUTH'
export const LOGIN_TOGGLE = 'LOGIN_TOGGLE'
export const LOGIN = 'LOGIN'
export const LOGOUT = 'LOGOUT'
export const AUTH_SESSION = 'OH_AUTH_SESSION'

export function initAuth () {
  return {
    type: INIT_AUTH,
    payload: !!sessionStorage.getItem(AUTH_SESSION)
  }
}

export function toggleLoginModal () {
  return {
    type: LOGIN_TOGGLE
  }
}

export function logout () {
  sessionStorage.removeItem(AUTH_SESSION)
  const payload = axios.get('/web/admin/logout')
  return {
    type: LOGOUT,
    payload
  }
}

export function login () {
  sessionStorage.setItem(AUTH_SESSION, true)
  return {
    type: LOGIN,
    payload: true
  }
}

export function loginError () {
  sessionStorage.setItem(AUTH_SESSION, false)
  return {
    type: LOGIN,
    payload: false
  }
}

export function submitLogin ({ username, password }) {
  return (dispatch) => {
    const params = new URLSearchParams()
    params.append('username', username)
    params.append('password', password)
    return axios.post('/web/admin/login', params)
      .then((res) => dispatch(login()))
      .catch((res) => dispatch(loginError()))
  }
}
