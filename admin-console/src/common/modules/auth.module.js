import axios from 'axios'
import { hashHistory } from 'react-router'

// ------------------------------------
// Constants
// ------------------------------------
export const INIT_AUTH = 'INIT_AUTH'
export const LOGIN_TOGGLE = 'LOGIN_TOGGLE'
export const LOGIN = 'LOGIN'
export const LOGOUT = 'LOGOUT'
export const AUTH_SESSION = 'OH_AUTH_SESSION'

// ------------------------------------
// Actions
// ------------------------------------

export const initAuth = () =>
  (dispatch, getState) => {
    const { auth: { authUser } } = getState()
    const localSession = sessionStorage.getItem(AUTH_SESSION)
    if (localSession) {
      return dispatch(login())
    }

    return dispatch({ type: INIT_AUTH, payload: authUser })
  }

export const toggleLoginModal = () => {
  return {
    type: LOGIN_TOGGLE
  }
}

export const logout = () => {
  sessionStorage.removeItem(AUTH_SESSION)
  const payload = axios.get('/web/admin/logout')
  hashHistory.push('/')
  return {
    type: LOGOUT,
    payload
  }
}

export const login = () =>
  (dispatch) => {
    axios.get('/web/admin/auth')
      .then(({ data }) => {
        sessionStorage.setItem(AUTH_SESSION, true)
        dispatch({ type: LOGIN, payload: data })
      })
      .catch(() => {
        dispatch(logout())
      })
  }

export const loginError = () => {
  sessionStorage.removeItem(AUTH_SESSION)
  return {
    type: LOGIN,
    payload: null
  }
}

export const submitLogin = ({ username, password }) => {
  return (dispatch) => {
    const params = new URLSearchParams()
    params.append('username', username)
    params.append('password', password)
    return axios.post('/web/admin/login', params)
      .then((res) => dispatch(login()))
      .catch((res) => dispatch(loginError()))
  }
}

export const actions = {
  initAuth,
  toggleLoginModal,
  logout,
  login,
  loginError,
  submitLogin
}

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [INIT_AUTH]: (state, { payload }) => ({ ...state, authUser: payload }),
  [LOGOUT]: (state) => ({ ...state, authUser: null }),
  [LOGIN_TOGGLE]: (state) => ({
    ...state,
    loginModalOpen: !state.loginModalOpen,
    loginErrors: null
  }),
  [LOGIN]: (state, { payload }) => ({ ...state, loginModalOpen: false, authUser: payload })
}

// ------------------------------------
// Reducer
// ------------------------------------
export const initialState = {
  loginModalOpen: false,
  authUser: null,
  loginErrors: null
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
