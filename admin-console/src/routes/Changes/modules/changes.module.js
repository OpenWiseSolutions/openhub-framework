import axios from 'axios'

// ------------------------------------
// Constants
// ------------------------------------
const GET_CHANGES_SUCCESS = 'GET_CHANGES_SUCCESS'
const GET_CHANGES_ERROR = 'GET_CHANGES_ERROR'

// ------------------------------------
// Actions
// ------------------------------------

export const getChanges = () => (dispatch) => {
  axios.get('/web/admin/changes')
    .then(({ data }) => {
      dispatch({ type: GET_CHANGES_SUCCESS, payload: data })
    })
    .catch(() => {
      dispatch({ type: GET_CHANGES_ERROR })
    })
}

export const actions = {
  getChanges
}

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [GET_CHANGES_SUCCESS]: (state, { payload }) => ({ ...state, changesData: payload })
}

// ------------------------------------
// Reducer
// ------------------------------------
export const initialState = {
  changesData: null
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
