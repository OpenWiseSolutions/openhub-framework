import CoreLayout from '../layouts/CoreLayout/containers/coreLayout.container'
import { initAuth } from '../common/actions/auth.actions'
import Home from './Home'

export const createRoutes = (store) => ({
  path: '/',
  component: CoreLayout,
  indexRoute: Home,
  childRoutes: [],
  onEnter: () => {
    store.dispatch(initAuth())
  }
})

export default createRoutes
