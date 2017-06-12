import { injectReducer } from '../../store/reducers'

export default (store) => ({
  path: '/messages',
  getComponent (nextState, cb) {
    require.ensure([], (require) => {
      const nodes = require('./containers/messages.container').default
      const reducer = require('./modules/messages.module').default
      injectReducer(store, { key: 'messages', reducer })
      cb(null, nodes)
    }, 'messages')
  }
})
