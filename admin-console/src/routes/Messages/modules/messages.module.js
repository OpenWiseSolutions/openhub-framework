import moment from 'moment'
import { pipe, pickBy, evolve } from 'ramda'
import { fetchMessages } from '../../../services/messages.service'

// ------------------------------------
// Constants
// ------------------------------------
export const GET_MESSAGES_SUCCESS = 'GET_MESSAGES_SUCCESS'

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

export const getMessages = (filter = {}) =>
  (dispatch) => {
    const receivedFrom = filter.receivedFrom || moment().subtract(1, 'minute').format()
    const payload = { ...filter, receivedFrom }

    const transformations = {
      receivedFrom: validDate,
      lastChangeFrom: validDate,
      receivedTo: validDate,
      lastChangeTo: validDate
    }

    const safePayload = pipe(
      evolve(transformations),
      pickBy(nonEmpty)
    )(payload)

    fetchMessages(safePayload)
      .then(({ data }) => {
        dispatch(getMessagesSuccess(data))
      })
  }

export const actions = {
  getMessages
}

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [GET_MESSAGES_SUCCESS]: (state, { payload }) => ({ ...state, messages: payload })
}

// ------------------------------------
// Reducer
// ------------------------------------
const initialState = {
  messages: []
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
