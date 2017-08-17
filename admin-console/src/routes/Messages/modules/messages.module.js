import { toastr } from 'react-redux-toastr'
import moment from 'moment'
import { pipe, pickBy, evolve, assoc } from 'ramda'
import { fetchMessages } from '../../../services/messages.service'

// ------------------------------------
// Constants
// ------------------------------------
export const GET_MESSAGES_SUCCESS = 'GET_MESSAGES_SUCCESS'
export const UPDATE_FILTER = 'messages/update-filter'
export const RESET_FILTER = 'messages/reset-filter'

// ------------------------------------
// Helpers
// ------------------------------------
const validDate = (d) => {
  if (d.constructor === Array) {
    return d ? moment(d[0]).format() : ''
  } else {
    return d ? moment(d).format() : ''
  }
}
const nonEmpty = (val, key) => val !== '' && val !== null && val !== undefined

// ------------------------------------
// Actions
// ------------------------------------

export const getMessagesSuccess = (data) => ({
  type: GET_MESSAGES_SUCCESS,
  payload: data
})

export const getMessages = () =>
  (dispatch, getState) => {
    const { messages: { filter } } = getState()
    const transformations = {
      receivedFrom: validDate,
      lastChangeFrom: validDate,
      receivedTo: validDate,
      lastChangeTo: validDate
    }

    const safePayload = pipe(
      evolve(transformations),
      pickBy(nonEmpty)
    )(filter)

    fetchMessages(safePayload)
      .then(({ data }) => {
        dispatch(getMessagesSuccess(data))
      })
      .catch(() => {
        toastr.error('Unable to fetch messages')
      })
  }

export const updateFilter = (field, value) =>
  (dispatch) => {
    dispatch({
      type: UPDATE_FILTER,
      field,
      value
    })
  }

export const resetFilter = () =>
  (dispatch) => {
    dispatch({
      type: RESET_FILTER
    })
  }

export const actions = {
  getMessages,
  updateFilter,
  resetFilter
}

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [GET_MESSAGES_SUCCESS]: (state, { payload }) => ({ ...state, messages: payload }),
  [UPDATE_FILTER]: (state, { field, value }) => ({ ...state, filter: assoc(field, value, state.filter) }),
  [RESET_FILTER]: (state) => ({ ...state, filter: initialState.filter })
}

// ------------------------------------
// Reducer
// ------------------------------------
const initialState = {
  messages: [],
  filter: {
    receivedFrom: moment().subtract(5, 'minute').format(),
    lastChangeFrom: '',
    sourceSystem: '',
    processId: '',
    errorCode: '',
    operationName: '',
    receivedTo: '',
    lastChangeTo: '',
    correlationId: '',
    state: '',
    serviceName: '',
    fulltext: ''
  }
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
