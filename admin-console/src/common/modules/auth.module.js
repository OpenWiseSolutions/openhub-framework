import { hashHistory } from 'react-router'
import { toastr } from 'react-redux-toastr'
import { login, userInfo, logout } from '../../services/auth.service'
import { fetchConsoleConfig } from '../../services/appConfig.service'

// ------------------------------------
// Constants
// ------------------------------------
export const INIT_AUTH = 'INIT_AUTH'
export const LOGIN_TOGGLE = 'LOGIN_TOGGLE'
export const LOGIN_CLOSE = 'LOGIN_CLOSE'
export const LOGIN_SUCCESS = 'LOGIN_SUCCESS'
export const LOGOUT_SUCCESS = 'LOGOUT_SUCCESS'
export const GET_APP_CONFIG_SUCCESS = 'GET_APP_CONFIG_SUCCESS'

// storage
export const AUTH_SESSION = 'OH_AUTH_SESSION'

// ------------------------------------
// Actions
// ------------------------------------

export const initAuth = () =>
  (dispatch, getState) => {
    const { auth: { userData } } = getState()
    const localSession = sessionStorage.getItem(AUTH_SESSION)
    if (localSession) {
      return dispatch(loginSuccess())
    }
    return dispatch({ type: INIT_AUTH, payload: userData })
  }

export const toggleLoginModal = () => {
  return {
    type: LOGIN_TOGGLE
  }
}

export const closeModal = () => {
  return {
    type: LOGIN_CLOSE
  }
}

export const getConfig = () => (dispatch) => {
  return fetchConsoleConfig()
    .then((data) => {
      dispatch({
        type: GET_APP_CONFIG_SUCCESS,
        payload: data.config
      })
    })
    .catch(() => {
      dispatch(logoutUser())
      toastr.error('Failed to retrieve console config!')
    })
}

export const logoutUser = () => (dispatch) => {
  sessionStorage.removeItem(AUTH_SESSION)
  return logout()
    .then(() => {
      hashHistory.push('/')
      dispatch({
        type: LOGOUT_SUCCESS
      })
    })
}

export const loginSuccess = () =>
  (dispatch) => {
    dispatch(closeModal())
    return userInfo()
      .then((payload) => {
        sessionStorage.setItem(AUTH_SESSION, true)
        dispatch({ type: LOGIN_SUCCESS, payload })
        dispatch(getConfig())
      })
      .catch(() => {
        dispatch(logout())
      })
  }

export const loginError = (res) => () => {
  sessionStorage.removeItem(AUTH_SESSION)
  toastr.error('Login Failed!')
  // todo error handler
}

export const submitLogin = ({ username, password }) => {
  return (dispatch) => {
    const params = new URLSearchParams()
    params.append('username', username)
    params.append('password', password)
    return login(params)
      .then((res) => dispatch(loginSuccess()))
      .catch((res) => dispatch(loginError(res)))
  }
}

export const actions = {
  initAuth,
  toggleLoginModal,
  logoutUser,
  login,
  loginError,
  submitLogin
}

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [INIT_AUTH]: (state, { payload }) => ({ ...state, userData: payload }),
  [LOGOUT_SUCCESS]: (state) => ({ ...state, userData: null }),
  [LOGIN_TOGGLE]: (state) => ({ ...state, loginModalOpen: !state.loginModalOpen }),
  [LOGIN_CLOSE]: (state) => ({ ...state, loginModalOpen: false }),
  [LOGIN_SUCCESS]: (state, { payload }) => ({ ...state, userData: payload }),
  [GET_APP_CONFIG_SUCCESS]: (state, { payload }) => ({ ...state, config: payload })
}

// ------------------------------------
// Reducer
// ------------------------------------
export const initialState = {
  // todo zmenit nazvy ?
  loginModalOpen: false,
  userData: null,
  config: null
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
