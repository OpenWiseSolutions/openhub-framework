import React, { Component } from 'react'
import Radium, { StyleRoot } from 'radium'
import Navbar from '../../../common/components/Navbar/Navbar'
import Sidebar from '../../../common/components/Sidebar/Sidebar'
import styles from './coreLayout.styles'
import LoginModal from '../../../common/containers/loginModal.container'

@Radium
class CoreLayout extends Component {

  render () {
    const { isAuth, children, sidebarExtended, navbarUserExpanded, actions, authActions } = this.props
    const bodyStyles = [
      styles.body,
      sidebarExtended && isAuth && styles.body.extended
    ]

    return (
      <StyleRoot>
        <div style={styles.main}>
          <Sidebar extended={sidebarExtended && isAuth} />
          <LoginModal />
          <div style={bodyStyles}>
            <Navbar
              isAuth={isAuth}
              logout={authActions.logout}
              navbarUserExpanded={navbarUserExpanded}
              toggleUser={actions.toggleNavbarUser}
              toggleLoginModal={authActions.toggleLoginModal}
              toggleSidebar={actions.toggleSidebar} />
            <div className='core-layout-contents'>
              {children}
            </div>
          </div>
        </div>
      </StyleRoot>
    )
  }
}

CoreLayout.propTypes = {
  children: React.PropTypes.element.isRequired,
  sidebarExtended: React.PropTypes.bool,
  actions: React.PropTypes.object,
  authActions: React.PropTypes.object,
  navbarUserExpanded: React.PropTypes.bool,
  isAuth: React.PropTypes.bool
}

export default CoreLayout
