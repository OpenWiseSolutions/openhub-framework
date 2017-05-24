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
      userData,
      children,
      sidebarExtended,
      navbarUserExpanded,
      actions,
      authActions,
      config
    } = this.props

    const bodyStyles = [
      styles.body,
      sidebarExtended && userData && styles.body.extended
    ]

    return (
      <StyleRoot>
        <div style={styles.main}>
          { config &&
          <Sidebar config={config.menu} extended={sidebarExtended && !!userData} />}
          <LoginModal />
          <div style={bodyStyles}>
            <Navbar
              userData={userData}
              logout={authActions.logoutUser}
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
  userData: PropTypes.object,
  config: PropTypes.object
}

export default CoreLayout
