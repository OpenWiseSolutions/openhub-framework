import axios from 'axios'

// ------------------------------------
// Constants
// ------------------------------------
const GET_ENVIRONMENT_SUCCESS = 'GET_ENVIRONMENT_SUCCESS'
const GET_ENVIRONMENT_ERROR = 'GET_ENVIRONMENT_ERROR'
const GET_CONFIG_SUCCESS = 'GET_CONFIG_SUCCESS'
const GET_CONFIG_ERROR = 'GET_CONFIG_ERROR'

// ------------------------------------
// Actions
// ------------------------------------

export const getEnvironmentData = () => (dispatch) => {
  axios.get('/web/admin/mgmt/env')
    .then(({ data }) => {
      dispatch({ type: GET_ENVIRONMENT_SUCCESS, payload: data })
    })
    .catch(() => {
      dispatch({ type: GET_ENVIRONMENT_ERROR })
    })
}

export const getConfigData = () => (dispatch) => {
  axios.get('/web/admin/mgmt/configprops')
    .then(({ data }) => {
      dispatch({ type: GET_CONFIG_SUCCESS, payload: data })
    })
    .catch(() => {
      dispatch({ type: GET_CONFIG_ERROR })
    })
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
