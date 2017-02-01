import { LOGOUT, AUTH_SESSION } from '../common/actions/auth.actions'

export default (store) => (next) => (action) => {
  if (action.error) {
    sessionStorage.removeItem(AUTH_SESSION)
    next({ type: LOGOUT })
  } else {
    next(action)
  }
}
