// ------------------------------------
// Constants
// ------------------------------------
export const UPDATE_TITLE = 'UPDATE_TITLE'
export const TOGGLE_SIDEBAR = 'TOGGLE_SIDEBAR'

// ------------------------------------
// Actions
// ------------------------------------
export const updateTitle = () => (dispatch, getState) => {
  const { auth: { userData } } = getState()
  const hash = window.location.hash.substr(1)
  let title = ''
  switch (hash.split('/')[1]) {
    case '':
      title = 'Dashboard'
      break
    case 'messages':
      title = 'Messages'
      break
    case 'nodes':
      title = 'Cluster Nodes'
      break
    case 'wsdl':
      title = 'WSDL'
      break
    case 'config-params':
      title = 'Config Parameters'
      break
    case 'config-logging':
      title = 'Config Logging'
      break
    case 'environment-properties':
      title = 'Environment Properties'
      break
    case 'alerts':
      title = 'Alerts'
      break
    case 'errors-overview':
      title = 'Errors Overview'
      break
    case 'changes':
      title = 'Changes'
      break
    default:
      title = ''
  }

  if (!userData) {
    title = 'Login'
  }

  dispatch({
    type: UPDATE_TITLE,
    title
  })
}

export const toggleSidebar = (sidebar) => ({
  type: TOGGLE_SIDEBAR,
  sidebar
})

export const actions = {
  toggleSidebar,
  updateTitle
}

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [UPDATE_TITLE]: (state, { title }) => ({ ...state, title }),
  [TOGGLE_SIDEBAR]: (state, { sidebar }) => ({ ...state, sidebar })
}

// ------------------------------------
// Reducer
// ------------------------------------
const initialState = {
  title: '',
  sidebar: true
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
