import { fetchErrorCatalog } from '../../../services/errors.service.js'

// ------------------------------------
// Constants
// ------------------------------------
const GET_ENVIRONMENT_SUCCESS = 'GET_ENVIRONMENT_SUCCESS'

// ------------------------------------
// Actions
// ------------------------------------

export const getErrorsOverviewSuccess = (payload) =>
  ({ type: GET_ENVIRONMENT_SUCCESS, payload })

export const getErrorsOverview = () => (dispatch) => {
  return fetchErrorCatalog()
    .then((data) => {
      dispatch(getErrorsOverviewSuccess(data))
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
