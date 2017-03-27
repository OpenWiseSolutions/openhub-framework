import CoreLayout from '../layouts/CoreLayout/containers/coreLayout.container'
import { initAuth } from '../common/actions/auth.actions'
import { getOpenHubInfo } from '../routes/Home/modules/home.module'
import Home from './Home'
import ConfigParams from './ConfigParams'

export const createRoutes = (store) => ({
  path: '/',
  component: CoreLayout,
  indexRoute: Home(store),
  childRoutes: [
    ConfigParams(store)
  ],
  onEnter: () => {
    store.dispatch(initAuth())
    store.dispatch(getOpenHubInfo())
  }
})

export default createRoutes
