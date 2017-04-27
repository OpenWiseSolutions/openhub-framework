import { injectReducer } from '../../store/reducers'

export default (store) => ({
  path: '/environment-properties',
  getComponent (nextState, cb) {
    require.ensure([], (require) => {
      const configParams = require('./containers/environmentProperties.container').default
      const reducer = require('./modules/environmentProperties.module').default
      injectReducer(store, { key: 'environmentProperties', reducer })
      cb(null, configParams)
    }, 'environmentProperties')
  }
})
