import { assoc } from 'ramda'
import { fetchCatalog } from '../../services/catalog.service'

// ------------------------------------
// Constants
// ------------------------------------
export const SET_CATALOG = 'catalogs/set'

// ------------------------------------
// Actions
// ------------------------------------

export const setCatalog = (name, data) => ({
  type: SET_CATALOG,
  name,
  data
})

export const getCatalog = (name) => (dispatch) => {
  fetchCatalog(name)
    .then(({ data }) => {
      dispatch(setCatalog(name, data))
    })
}

export const actions = {
  getCatalog
}

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [SET_CATALOG]: (state, { name, data }) => assoc(name, data, state)
}

// ------------------------------------
// Reducer
// ------------------------------------
export const initialState = {}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
