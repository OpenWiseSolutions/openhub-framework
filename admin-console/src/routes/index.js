import CoreLayout from '../layouts/CoreLayout/containers/coreLayout.container'
import { initAuth } from '../common/modules/auth.module'
import { getOpenHubInfo } from '../routes/Home/modules/home.module'
import Home from './Home'
import ConfigParams from './ConfigParams'
import ConfigLogging from './ConfigLogging'
import Changes from './Changes'
import EnvironmentProperties from './EnvironmentProperties'
import ErrorsOverview from './ErrorsOverview'
import Nodes from './Nodes'

export const createRoutes = (store) => ({
  path: '/',
  component: CoreLayout,
  indexRoute: Home(store),
  childRoutes: [
    ConfigParams(store),
    ConfigLogging(store),
    Changes(store),
    EnvironmentProperties(store),
    ErrorsOverview(store),
    Nodes(store)
  ],
  onEnter: () => {
    store.dispatch(initAuth())
    store.dispatch(getOpenHubInfo())
  }
})

export default createRoutes
