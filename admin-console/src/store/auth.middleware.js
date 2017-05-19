import { logoutUser } from '../common/modules/auth.module'

export default (store) => (next) => (action) => {
  if (action.type === 'LOCATION_CHANGE') {
    const { auth: { userData } } = store.getState()
    if (!userData) {
      store.dispatch(logoutUser())
      return
    }
  }
  next(action)
}
