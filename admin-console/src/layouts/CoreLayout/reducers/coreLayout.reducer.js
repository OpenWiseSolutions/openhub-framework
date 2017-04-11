import { SIDEBAR_TOGGLE, NAVBAR_USER_TOGGLE } from '../actions/coreLayout.actions'
import { LOGOUT } from '../../../common/modules/auth.module'

const defaultState = {
  sidebarExtended: true,
  navbarUserExpanded: false
}

export default function (state = defaultState, action) {
  switch (action.type) {

    case LOGOUT:
      return {
        ...state,
        navbarUserExpanded: false
      }

    case SIDEBAR_TOGGLE:

      return {
        ...state,
        sidebarExtended: !state.sidebarExtended
      }

    case NAVBAR_USER_TOGGLE:
      return {
        ...state,
        navbarUserExpanded: !state.navbarUserExpanded
      }

    default:
      return state
  }
}
