import { injectReducer } from '../../store/reducers'

export default (store) => ({
  path: '/config-params',
  getComponent (nextState, cb) {
    require.ensure([], (require) => {
      const configParams = require('./containers/configParams.container').default
      const reducer = require('./modules/configParams.module').default
      injectReducer(store, { key: 'configParams', reducer })
      cb(null, configParams)
    }, 'configParams')
  }
})
