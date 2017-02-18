import objectPath from 'object-path'
import axios from 'axios'

// ------------------------------------
// Constants
// ------------------------------------
export const GET_OPENHUB_INFO = 'GET_OPENHUB_INFO'
export const GET_HEALTH_INFO = 'GET_HEALTH_INFO'
export const GET_METRICS_INFO = 'GET_METRICS_INFO'

// ------------------------------------
// Actions
// ------------------------------------

export const getHealthInfo = () => {
  const payload = axios.get('/web/admin/mgmt/health')
  return {
    type: GET_HEALTH_INFO,
    payload
  }
}

export const getMetricsInfo = () => {
  const payload = axios.get('/web/admin/mgmt/metrics')
  return {
    type: GET_METRICS_INFO,
    payload
  }
}

export const getOpenHubInfo = () => {
  const payload = axios.get('/web/admin/mgmt/info')
  return {
    type: GET_OPENHUB_INFO,
    payload
  }
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
  [GET_HEALTH_INFO]: (state, { payload }) => ({ ...state, healthInfo: payload.data }),
  [GET_OPENHUB_INFO]: (state, { payload }) => ({ ...state, openHubInfo: objectPath.get(payload, 'data.app') }),
  [GET_METRICS_INFO]: (state, { payload }) => ({ ...state, metricsInfo: payload.data })
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
