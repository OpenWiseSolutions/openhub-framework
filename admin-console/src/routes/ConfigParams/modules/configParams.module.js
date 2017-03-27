import axios from 'axios'
import { find, propEq } from 'ramda'

// ------------------------------------
// Constants
// ------------------------------------

export const GET_CONFIG_PARAMS = 'GET_CONFIG_PARAMS'
export const OPEN_PARAM = 'OPEN_PARAM'
export const CLOSE_PARAM = 'CLOSE_PARAM'
export const UPDATE_PARAM_INIT = 'UPDATE_PARAM_INIT'
export const UPDATE_PARAM_SUCCESS = 'UPDATE_PARAM_SUCCESS'
export const UPDATE_PARAM_ERROR = 'UPDATE_PARAM_ERROR'

// ------------------------------------
// Actions
// ------------------------------------

export const getConfigParams = () => {
  const payload = axios.get('/web/admin/config-params')
  return {
    type: GET_CONFIG_PARAMS,
    payload
  }
}

export const openParam = (id) => {
  return (dispatch, getState) => {
    const { configParams: { configParams } } = getState()
    const paramDetail = find(propEq('id', id))(configParams.data)
    dispatch({
      type: OPEN_PARAM,
      payload: paramDetail
    })
  }
}

export const updateParam = (code, payload) => {
  return (dispatch) => {
    dispatch({ type: UPDATE_PARAM_INIT })
    axios.put(`/web/admin/config-params/${code}`, payload)
      .then(() => {
        dispatch({ type: UPDATE_PARAM_SUCCESS })
      })
      .catch(() => {
        dispatch({ type: UPDATE_PARAM_ERROR })
      })
  }
}

export const closeParam = (id) => ({
  type: CLOSE_PARAM
})

export const actions = {
  getConfigParams,
  openParam,
  closeParam,
  updateParam
}

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [GET_CONFIG_PARAMS]: (state, { payload }) => ({ ...state, configParams: payload.data }),
  [OPEN_PARAM]: (state, { payload }) => ({ ...state, paramDetail: payload, updating: false, updateError: false }),
  [CLOSE_PARAM]: (state) => ({ ...state, paramDetail: null }),
  [UPDATE_PARAM_INIT]: (state) => ({ ...state, updating: true, updateError: false }),
  [UPDATE_PARAM_SUCCESS]: (state) => ({ ...state, updating: false, paramDetail: null }),
  [UPDATE_PARAM_ERROR]: (state) => ({ ...state, updating: false, updateError: true })
}

// ------------------------------------
// Reducer
// ------------------------------------
const initialState = {
  configParams: null,
  paramDetail: null,
  updateError: null,
  updating: false
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
