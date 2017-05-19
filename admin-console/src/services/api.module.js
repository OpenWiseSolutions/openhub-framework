import { assoc, dissoc } from 'ramda'

// ------------------------------------
// Constants
// ------------------------------------
export const API_INIT = 'API_INIT'
export const FETCH_START = 'FETCH_START'
export const FETCH_STOP = 'FETCH_STOP'
export const FETCH_LOG = 'FETCH_LOG'
export const FETCH_ERROR = 'FETCH_ERROR'
export const RESET_ERROR = 'RESET_ERROR'

// ------------------------------------
// Actions
// ------------------------------------

export const apiInitialised = (base) => ({
  type: API_INIT,
  payload: base
})

export const fetchLog = (log) => ({
  type: FETCH_LOG,
  payload: log
})

export const fetchStart = (path, config) => (dispatch) => {
  if (__DEV__) {
    dispatch(fetchLog({
      type: FETCH_START,
      path,
      config
    }))
  }
  dispatch(resetError(path))
  dispatch({
    type: FETCH_START,
    payload: path
  })
}

export const fetchStop = (path) => (dispatch) => {
  if (__DEV__) {
    dispatch(fetchLog({
      type: FETCH_STOP,
      path
    }))
  }
  dispatch({
    type: FETCH_STOP,
    payload: path
  })
}

export const fetchError = (path, response) => (dispatch) => {
  dispatch(fetchStop(path))
  dispatch({
    type: FETCH_ERROR,
    payload: {
      path,
      response
    }
  })
}

export const resetError = (path) => ({
  type: RESET_ERROR,
  payload: path
})

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [FETCH_START]: (state, { payload }) => ({ ...state, active: assoc(payload, true, state.active) }),
  [FETCH_STOP]: (state, { payload }) => ({ ...state, active: dissoc(payload, state.active) }),
  [RESET_ERROR]: (state, { payload }) => ({ ...state, failed: dissoc(payload, state.failed) }),
  [FETCH_ERROR]: (state, { payload: { path, response } }) => ({
    ...state, failed: assoc(path, response, state.failed)
  })
}

// ------------------------------------
// Reducer
// ------------------------------------
export const initialState = {
  active: {},
  failed: {}
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
