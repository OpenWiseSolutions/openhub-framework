import { injectReducer } from '../../store/reducers'

export default (store) => ({
  path: '/wsdl',
  getComponent (nextState, cb) {
    require.ensure([], (require) => {
      const wsdl = require('./containers/wsdl.container').default
      const reducer = require('./modules/wsdl.module').default
      injectReducer(store, { key: 'wsdl', reducer })
      cb(null, wsdl)
    }, 'wsdl')
  }
})
