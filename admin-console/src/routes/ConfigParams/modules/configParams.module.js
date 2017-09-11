import { omit, map, find, propEq, isNil } from 'ramda'
import { toastr } from 'react-redux-toastr'
import { fetchConfigParams, updateConfigParam } from '../../../services/configParams.service'

// ------------------------------------
// Constants
// ------------------------------------

export const GET_CONFIG_PARAMS_SUCCESS = 'GET_CONFIG_PARAMS_SUCCESS'
export const OPEN_PARAM = 'OPEN_PARAM'
export const CLOSE_PARAM = 'CLOSE_PARAM'
export const UPDATE_PARAM_INIT = 'UPDATE_PARAM_INIT'
export const UPDATE_PARAM_SUCCESS = 'UPDATE_PARAM_SUCCESS'
export const UPDATE_PARAM_ERROR = 'UPDATE_PARAM_ERROR'

// ------------------------------------
// Actions
// ------------------------------------

export const getConfigParamsSuccess = ({ data }) => {
  const normalizedData = map(map((i) => isNil(i) ? '' : i), data)
  return {
    type: GET_CONFIG_PARAMS_SUCCESS,
    payload: normalizedData
  }
}

export const getConfigParams = () => (dispatch) => {
  return fetchConfigParams()
    .then((res) => dispatch(getConfigParamsSuccess(res)))
}

export const openParam = (id) => {
  return (dispatch, getState) => {
    const { configParams: { configParams } } = getState()
    const paramDetail = find(propEq('id', id))(configParams)
    dispatch({
      type: OPEN_PARAM,
      payload: paramDetail
    })
  }
}

export const updateParam = (data) => {
  return (dispatch) => {
    const payload = omit(['code'], data)
    dispatch({ type: UPDATE_PARAM_INIT })

    if (payload.dataType === 'INT') {
      payload.defaultValue = parseInt(payload.defaultValue)
      payload.currentValue = parseInt(payload.currentValue)
    }

    if (payload.dataType === 'FLOAT') {
      payload.defaultValue = parseFloat(payload.defaultValue)
      payload.currentValue = parseFloat(payload.currentValue)
    }
    return updateConfigParam(data.code, { ...payload })
      .then(() => {
        dispatch(getConfigParams())
        dispatch({ type: UPDATE_PARAM_SUCCESS })
        dispatch(closeParam())
        toastr.success('Success', 'Parameter updated')
      })
      .catch(() => {
        dispatch({ type: UPDATE_PARAM_ERROR })
        toastr.error('Error', 'Parameter update failed')
      })
  }
}

export const closeParam = () => ({
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
  [GET_CONFIG_PARAMS_SUCCESS]: (state, { payload }) => ({ ...state, configParams: payload }),
  [OPEN_PARAM]: (state, { payload }) => ({ ...state, paramDetail: payload, updating: false, updateError: false }),
  [CLOSE_PARAM]: (state) => ({ ...state, paramDetail: null }),
  [UPDATE_PARAM_INIT]: (state) => ({ ...state, updating: true, updateError: false }),
  [UPDATE_PARAM_SUCCESS]: (state) => ({ ...state, updating: false, paramDetail: null }),
  [UPDATE_PARAM_ERROR]: (state) => ({ ...state, updating: false })
}

// ------------------------------------
// Reducer
// ------------------------------------
const initialState = {
  configParams: null,
  paramDetail: null,
  updating: false
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
