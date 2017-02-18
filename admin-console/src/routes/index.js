import CoreLayout from '../layouts/CoreLayout/containers/coreLayout.container'
import { initAuth } from '../common/actions/auth.actions'
import { getOpenHubInfo, getMetricsInfo } from '../routes/Home/modules/home.module'
import Home from './Home'

export const createRoutes = (store) => ({
  path: '/',
  component: CoreLayout,
  indexRoute: Home(store),
  childRoutes: [],
  onEnter: () => {
    store.dispatch(initAuth())
    store.dispatch(getOpenHubInfo())
    store.dispatch(getMetricsInfo())
  }
})

export default createRoutes
