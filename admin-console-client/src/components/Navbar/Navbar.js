import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import styles from './navbar.styles'
import MenuIcon from 'react-icons/lib/md/menu'
import UserButton from '../UserButton/UserButton'
// todo links import { IndexLink, Link } from 'react-router'

@Radium
class Navbar extends Component {
  render () {
    const { toggleSidebar, toggleUser, navbarUserExpanded } = this.props
    return (
      <div className='navbar-wrapper' style={styles.main}>
        <div className='sidebar-toggle' onClick={toggleSidebar} style={styles.left}>
          <MenuIcon style={styles.menuIcon} />
        </div>
        <div style={styles.right}>
          <UserButton expanded={navbarUserExpanded} toggle={toggleUser} name='Tomas Hanus' />
        </div>
      </div>
    )
  }
}

Navbar.propTypes = {
  toggleSidebar: PropTypes.func.isRequired,
  toggleUser: PropTypes.func,
  navbarUserExpanded: PropTypes.bool
}

export default Navbar
