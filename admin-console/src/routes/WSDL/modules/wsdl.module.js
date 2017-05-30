import { fetchWsdl } from '../../../services/wsdl.service'

// ------------------------------------
// Constants
// ------------------------------------
const GET_WSDL_SUCCESS = 'GET_WSDL_SUCCESS'

// ------------------------------------
// Actions
// ------------------------------------

export const getWsdlOverviewSuccess = (payload) =>
  ({ type: GET_WSDL_SUCCESS, payload })

export const getWsdlOverview = () => (dispatch) => {
  return fetchWsdl()
    .then(({ data }) => {
      dispatch(getWsdlOverviewSuccess(data))
    })
}

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [GET_WSDL_SUCCESS]: (state, { payload }) => ({ ...state, wsdlData: payload })
}

// ------------------------------------
// Reducer
// ------------------------------------
export const initialState = {
  wsdlData: null
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
