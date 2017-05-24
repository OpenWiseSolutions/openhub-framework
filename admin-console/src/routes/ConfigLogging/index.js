import { injectReducer } from '../../store/reducers'

export default (store) => ({
  path: '/config-logging',
  getComponent (nextState, cb) {
    require.ensure([], (require) => {
      const configLogging = require('./containers/configLogging.container').default
      const reducer = require('./modules/configLogging.module').default
      injectReducer(store, { key: 'configLogging', reducer })
      cb(null, configLogging)
    }, 'configLogging')
  }
})
