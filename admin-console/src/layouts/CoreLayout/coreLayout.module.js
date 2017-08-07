// ------------------------------------
// Constants
// ------------------------------------
export const UPDATE_TITLE = 'UPDATE_TITLE'

// ------------------------------------
// Actions
// ------------------------------------
export const updateTitle = () => {
  const hash = window.location.hash.substr(1)
  let title = ''
  switch (hash) {
    case '/':
      title = 'Dashboard'
      break
    case '/messages':
      title = 'Messages'
      break
    case '/nodes':
      title = 'Cluster Nodes'
      break
    case '/wsdl':
      title = 'WSDL'
      break
    case '/config-params':
      title = 'Config Parameters'
      break
    case '/config-logging':
      title = 'Config Logging'
      break
    case '/environment-properties':
      title = 'Environment Properties'
      break
    case '/alerts':
      title = 'Alerts'
      break
    case '/errors-overview':
      title = 'Errors Overview'
      break
    case '/changes':
      title = 'Changes'
      break
    default:
      title = ''
  }

  return ({
    type: UPDATE_TITLE,
    title
  })
}

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [UPDATE_TITLE]: (state, { title }) => ({ ...state, title })
}

// ------------------------------------
// Reducer
// ------------------------------------
const initialState = {
  title: 'Login'
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
