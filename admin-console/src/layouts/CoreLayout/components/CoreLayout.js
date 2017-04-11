import React, { PropTypes, Component } from 'react'
import Radium, { StyleRoot } from 'radium'
import Navbar from '../../../common/components/Navbar/Navbar'
import Sidebar from '../../../common/components/Sidebar/Sidebar'
import styles from './coreLayout.styles'
import LoginModal from '../../../common/containers/loginModal.container'

@Radium
class CoreLayout extends Component {

  render () {
    const {
      authUser,
      children,
      sidebarExtended,
      navbarUserExpanded,
      actions,
      authActions
    } = this.props

    const bodyStyles = [
      styles.body,
      sidebarExtended && authUser && styles.body.extended
    ]

    return (
      <StyleRoot>
        <div style={styles.main}>
          <Sidebar extended={sidebarExtended && !!authUser} />
          <LoginModal />
          <div style={bodyStyles}>
            <Navbar
              authUser={authUser}
              logout={authActions.logout}
              navbarUserExpanded={navbarUserExpanded}
              toggleUser={actions.toggleNavbarUser}
              toggleLoginModal={authActions.toggleLoginModal}
              toggleSidebar={actions.toggleSidebar} />
            <div style={styles.contents} >
              {children}
            </div>
          </div>
        </div>
      </StyleRoot>
    )
  }
}

CoreLayout.propTypes = {
  children: PropTypes.element.isRequired,
  sidebarExtended: PropTypes.bool,
  actions: PropTypes.object,
  authActions: PropTypes.object,
  navbarUserExpanded: PropTypes.bool,
  authUser: PropTypes.object
}

export default CoreLayout
