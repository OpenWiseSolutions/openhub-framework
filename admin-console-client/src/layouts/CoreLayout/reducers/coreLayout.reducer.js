import { SIDEBAR_EXTEND } from '../actions/coreLayout.actions'

const defaultUiState = {
  sidebarExtended: false
}

export default function (state = defaultUiState, action) {
  const ui = { ...state }

  switch (action.type) {
    case SIDEBAR_EXTEND:
      ui.sidebarExtended = !ui.sidebarExtended
      return ui

    default:
      return state
  }
}
