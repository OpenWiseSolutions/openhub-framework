import { combineReducers } from 'redux'
import locationReducer from './location'
import { reducer as toastrReducer } from 'react-redux-toastr'

import coreLayoutReducer from '../layouts/CoreLayout/reducers/coreLayout.reducer'
import authReducer from '../common/modules/auth.module'

export const makeRootReducer = (asyncReducers) => {
  return combineReducers({
    location: locationReducer,
    coreLayout: coreLayoutReducer,
    auth: authReducer,
    toastr: toastrReducer,
    ...asyncReducers
  })
}

export const injectReducer = (store, { key, reducer }) => {
  if (Object.hasOwnProperty.call(store.asyncReducers, key)) return

  store.asyncReducers[key] = reducer
  store.replaceReducer(makeRootReducer(store.asyncReducers))
}

export default makeRootReducer
