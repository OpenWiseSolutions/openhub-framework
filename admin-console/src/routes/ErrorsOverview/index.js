import { injectReducer } from '../../store/reducers'

export default (store) => ({
  path: '/errors-overview',
  getComponent (nextState, cb) {
    require.ensure([], (require) => {
      const configParams = require('./containers/errorsOverview.container').default
      const reducer = require('./modules/errorsOverview.module').default
      injectReducer(store, { key: 'errorsOverview', reducer })
      cb(null, configParams)
    }, 'errorsOverview')
  }
})
