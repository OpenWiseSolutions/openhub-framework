import { toastr } from 'react-redux-toastr'
import { fetchChanges } from '../../../services/changes.service.js'

// ------------------------------------
// Constants
// ------------------------------------
const GET_CHANGES_SUCCESS = 'GET_CHANGES_SUCCESS'

// ------------------------------------
// Actions
// ------------------------------------

export const getChangesSuccess = (payload) => ({
  type: GET_CHANGES_SUCCESS,
  payload
})

export const getChanges = () => (dispatch) => {
  return fetchChanges()
    .then((response) => {
      dispatch(getChangesSuccess(response))
    })
    .catch(() => {
      // todo error
      toastr.error('Error fetching changes!')
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
