import { injectReducer } from '../../store/reducers'

export default (store) => ({
  getComponent (nextState, cb) {
    require.ensure([], (require) => {
      const home = require('./containers/home.container').default
      const reducer = require('./modules/home.module').default
      injectReducer(store, { key: 'home', reducer })
      cb(null, home)
    }, 'home')
  }
})
