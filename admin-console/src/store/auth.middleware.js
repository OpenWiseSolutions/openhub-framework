import { path } from 'ramda'
import { logout } from '../common/modules/auth.module'

export default (store) => (next) => (action) => {
  if (path(['payload', 'response', 'status'], action) === 401) {
    next(logout())
  } else {
    next(action)
  }
}
