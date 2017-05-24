import { injectReducer } from '../../store/reducers'

export default (store) => ({
  path: '/changes',
  getComponent (nextState, cb) {
    require.ensure([], (require) => {
      const configLogging = require('./containers/changes.container').default
      const reducer = require('./modules/changes.module').default
      injectReducer(store, { key: 'changes', reducer })
      cb(null, configLogging)
    }, 'changes')
  }
})
