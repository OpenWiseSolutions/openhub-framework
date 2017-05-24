import { fetchEnvironmentData, fetchEnvironmentConfig } from '../../../services/environment.service.js'

// ------------------------------------
// Constants
// ------------------------------------
const GET_ENVIRONMENT_SUCCESS = 'GET_ENVIRONMENT_SUCCESS'
const GET_CONFIG_SUCCESS = 'GET_CONFIG_SUCCESS'

// ------------------------------------
// Actions
// ------------------------------------

export const getEnvironmentDataSuccess = (payload) =>
  ({ type: GET_ENVIRONMENT_SUCCESS, payload })

export const getEnvironmentData = () => (dispatch) => {
  return fetchEnvironmentData()
    .then((data) => dispatch(getEnvironmentDataSuccess(data)))
}

export const getConfigDataSuccess = (payload) =>
  ({ type: GET_CONFIG_SUCCESS, payload })

export const getConfigData = () => (dispatch) => {
  return fetchEnvironmentConfig()
    .then((data) => dispatch(getConfigDataSuccess(data)))
}

export const actions = {
  getEnvironmentData
}

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [GET_ENVIRONMENT_SUCCESS]: (state, { payload }) => ({ ...state, environmentData: payload }),
  [GET_CONFIG_SUCCESS]: (state, { payload }) => ({ ...state, configData: payload })
}

// ------------------------------------
// Reducer
// ------------------------------------
export const initialState = {
  environmentData: null,
  configData: null
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
