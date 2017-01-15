import React, { Component } from 'react'
import Radium from 'radium'
import Navbar from '../../../components/Navbar/Navbar'
import Sidebar from '../../../components/Sidebar/Sidebar'
import styles from './coreLayout.styles'

@Radium
class CoreLayout extends Component {

  render () {
    const { children, sidebarExtended, navbarUserExpanded, actions } = this.props
    const bodyStyles = [
      styles.body,
      sidebarExtended && styles.body.extended
    ]

    return (
      <div style={styles.main}>
        <Sidebar extended={sidebarExtended} />
        <div style={bodyStyles}>
          <Navbar
            navbarUserExpanded={navbarUserExpanded}
            toggleUser={actions.toggleNavbarUser}
            toggleSidebar={actions.toggleSidebar} />
          <div className='core-layout-contents'>
            {children}
          </div>
        </div>
      </div>
    )
  }
}

CoreLayout.propTypes = {
  children: React.PropTypes.element.isRequired,
  sidebarExtended: React.PropTypes.bool,
  actions: React.PropTypes.object,
  navbarUserExpanded: React.PropTypes.bool
}

export default CoreLayout
