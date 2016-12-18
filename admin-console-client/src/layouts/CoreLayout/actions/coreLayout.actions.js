export const SIDEBAR_TOGGLE = 'SIDEBAR_TOGGLE'
export const NAVBAR_USER_TOGGLE = 'NAVBAR_USER_TOGGLE'

export function toggleSidebar () {
  return {
    type: SIDEBAR_TOGGLE
  }
}

export function toggleNavbarUser () {
  return {
    type: NAVBAR_USER_TOGGLE
  }
}
