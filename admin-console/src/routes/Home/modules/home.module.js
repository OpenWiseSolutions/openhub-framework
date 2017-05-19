import { fetchHealth, fetchInfo, fetchMetrics } from '../../../services/home.service.js'

// ------------------------------------
// Constants
// ------------------------------------
export const GET_HEALTH_INFO_SUCCESS = 'GET_HEALTH_INFO_SUCCESS'
export const GET_OPENHUB_INFO_SUCCESS = 'GET_OPENHUB_INFO_SUCCESS'
export const GET_METRICS_INFO_SUCCESS = 'GET_METRICS_INFO_SUCCESS'

// ------------------------------------
// Actions
// ------------------------------------

export const getHealthInfoSuccess = (payload) =>
  ({ type: GET_HEALTH_INFO_SUCCESS, payload })

export const getHealthInfo = () => (dispatch) => {
  return fetchHealth()
    .then((response) => dispatch(getHealthInfoSuccess(response)))
    // todo error
}

export const getMetricsInfoSuccess = (payload) =>
  ({ type: GET_METRICS_INFO_SUCCESS, payload })

export const getMetricsInfo = () => (dispatch) => {
  return fetchMetrics()
    .then((data) => dispatch(getMetricsInfoSuccess(data)))
    // todo error
}

const getOpenHubInfoSuccess = (payload) =>
  ({ type: GET_OPENHUB_INFO_SUCCESS, payload: payload.app })

export const getOpenHubInfo = () => (dispatch) => {
  return fetchInfo()
    .then((data) => dispatch(getOpenHubInfoSuccess(data)))
    // todo error
}

export const actions = {
  getHealthInfo,
  getOpenHubInfo,
  getMetricsInfo
}

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [GET_HEALTH_INFO_SUCCESS]: (state, { payload }) => ({ ...state, healthInfo: payload }),
  [GET_OPENHUB_INFO_SUCCESS]: (state, { payload }) => ({ ...state, openHubInfo: payload }),
  [GET_METRICS_INFO_SUCCESS]: (state, { payload }) => ({ ...state, metricsInfo: payload })
}

// ------------------------------------
// Reducer
// ------------------------------------
const initialState = {
  healthInfo: null,
  openHubInfo: null,
  metricsInfo: null
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
