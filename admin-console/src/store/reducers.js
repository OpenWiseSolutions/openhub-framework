import { combineReducers } from 'redux'
import locationReducer from './location'
import { reducer as toastrReducer } from 'react-redux-toastr'
import apiReducer from '../services/api.module'
import layoutReducer from '../layouts/CoreLayout/coreLayout.module'

import authReducer from '../common/modules/auth.module'

export const makeRootReducer = (asyncReducers) => {
  return combineReducers({
    location: locationReducer,
    auth: authReducer,
    toastr: toastrReducer,
    layout: layoutReducer,
    api: apiReducer,
    ...asyncReducers
  })
}

export const injectReducer = (store, { key, reducer }) => {
  if (Object.hasOwnProperty.call(store.asyncReducers, key)) return

  store.asyncReducers[key] = reducer
  store.replaceReducer(makeRootReducer(store.asyncReducers))
}

export default makeRootReducer
