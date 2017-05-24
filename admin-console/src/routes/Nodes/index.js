import { injectReducer } from '../../store/reducers'

export default (store) => ({
  path: '/nodes',
  getComponent (nextState, cb) {
    require.ensure([], (require) => {
      const nodes = require('./containers/nodes.container').default
      const reducer = require('./modules/nodes.module').default
      injectReducer(store, { key: 'nodes', reducer })
      cb(null, nodes)
    }, 'nodes')
  }
})
