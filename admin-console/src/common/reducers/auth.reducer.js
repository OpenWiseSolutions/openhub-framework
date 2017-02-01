import { LOGIN_TOGGLE, LOGIN, INIT_AUTH, LOGOUT } from '../actions/auth.actions'

const defaultState = {
  loginModalOpen: false,
  isAuth: false,
  loginErrors: null
}

export default function (state = defaultState, action) {
  switch (action.type) {

    case INIT_AUTH:
      return {
        ...state,
        isAuth: action.payload
      }

    case LOGOUT:
      return {
        ...state,
        isAuth: false
      }

    case LOGIN_TOGGLE:
      return {
        ...state,
        loginModalOpen: !state.loginModalOpen,
        loginErrors: null
      }

    case LOGIN:
      const _state = { ...state }
      if (action.payload) {
        _state.loginModalOpen = false
        _state.loginErrors = null
        _state.isAuth = true
      } else {
        _state.loginErrors = true
      }

      return _state

    default:
      return state
  }
}
