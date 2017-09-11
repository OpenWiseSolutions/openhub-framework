import { toastr } from 'react-redux-toastr'
import {
  fetchMessage,
  restartMessage,
  cancelMessage
} from '../../../services/messages.service'

// ------------------------------------
// Constants
// ------------------------------------
export const GET_MESSAGE_SUCCESS = 'GET_MESSAGE_SUCCESS'
export const GET_MESSAGE_INIT = 'GET_MESSAGE_INIT'

// ------------------------------------
// Actions
// ------------------------------------

export const getMessageSuccess = (payload) => ({
  type: GET_MESSAGE_SUCCESS,
  payload
})

export const getMessageInit = () => ({
  type: GET_MESSAGE_INIT
})

export const getMessage = (id) => (dispatch) => {
  dispatch(getMessageInit())
  return fetchMessage(id)
    .then((data) => dispatch(getMessageSuccess(data)))
}

export const restart = (id, total) =>
  (dispatch) => {
    toastr.confirm('Are you sure that you want to restart this message?', {
      onOk: () => {
        restartMessage(id, total)
          .then((data) => {
            if (data.result === 'OK') {
              toastr.success(data.resultDescription)
              dispatch(getMessage(id))
            } else {
              toastr.error(data.resultDescription)
            }
          })
          .catch(() => {
            toastr.error('Message restart failed')
          })
      }
    })
  }

export const cancel = (id) =>
  (dispatch) => {
    toastr.confirm('Are you sure that you want to cancel this message?', {
      onOk: () => {
        cancelMessage(id)
          .then((data) => {
            if (data.result === 'OK') {
              toastr.success(data.resultDescription)
              dispatch(getMessage(id))
            } else {
              toastr.error(data.resultDescription)
            }
          })
          .catch(() => {
            toastr.error('Message cancelation failed')
          })
      }
    })
  }

export const actions = {
  getMessage,
  restart,
  cancel
}

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [GET_MESSAGE_INIT]: (state) => ({ ...state, message: null }),
  [GET_MESSAGE_SUCCESS]: (state, { payload }) => ({ ...state, message: payload })
}

// ------------------------------------
// Reducer
// ------------------------------------
const initialState = {
  message: null
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
