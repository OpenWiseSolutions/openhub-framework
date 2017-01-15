import { SIDEBAR_TOGGLE, NAVBAR_USER_TOGGLE } from '../actions/coreLayout.actions'

const defaultUiState = {
  sidebarExtended: false,
  navbarUserExpanded: false
}

export default function (state = defaultUiState, action) {
  const ui = { ...state }

  switch (action.type) {
    case SIDEBAR_TOGGLE:
      ui.sidebarExtended = !ui.sidebarExtended
      return ui

    case NAVBAR_USER_TOGGLE:
      ui.navbarUserExpanded = !ui.navbarUserExpanded
      return ui

    default:
      return state
  }
}
