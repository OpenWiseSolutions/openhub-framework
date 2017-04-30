import axios from 'axios'

// ------------------------------------
// Constants
// ------------------------------------
const GET_ENVIRONMENT_SUCCESS = 'GET_ENVIRONMENT_SUCCESS'
const GET_ENVIRONMENT_ERROR = 'GET_ENVIRONMENT_ERROR'

// ------------------------------------
// Actions
// ------------------------------------

export const getErrorsOverview = () => (dispatch) => {
  axios.get('/web/admin/api/errors-catalog')
    .then(({ data }) => {
      dispatch({ type: GET_ENVIRONMENT_SUCCESS, payload: data })
    })
    .catch(() => {
      dispatch({ type: GET_ENVIRONMENT_ERROR })
    })
}

export const actions = {
  getErrorsOverview
}

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [GET_ENVIRONMENT_SUCCESS]: (state, { payload }) => ({ ...state, errorsData: payload })
}

// ------------------------------------
// Reducer
// ------------------------------------
export const initialState = {
  errorsData: null
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
