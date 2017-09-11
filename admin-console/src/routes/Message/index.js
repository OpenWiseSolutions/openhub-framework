import { injectReducer } from '../../store/reducers'

export default (store) => ({
  path: '/messages/:id',
  getComponent (nextState, cb) {
    require.ensure([], (require) => {
      const nodes = require('./containers/message.container').default
      const reducer = require('./modules/message.module').default
      injectReducer(store, { key: 'message', reducer })
      cb(null, nodes)
    }, 'message')
  }
})
