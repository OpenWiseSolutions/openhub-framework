import { path } from 'ramda'
import { logout } from '../common/actions/auth.actions'

export default (store) => (next) => (action) => {
  if (path(['payload', 'response', 'status'], action) === 401) {
    next(logout())
  } else {
    next(action)
  }
}
