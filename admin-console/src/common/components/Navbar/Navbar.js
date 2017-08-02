import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import styles from './navbar.styles'
import MenuIcon from 'react-icons/lib/md/menu'
import UserButton from '../UserButton/UserButton'
import Item from '../Item/Item'
import Anchor from '../Anchor/Anchor'

@Radium
class Navbar extends Component {
  render () {
    const { logout, toggleSidebar, toggleUser, toggleLoginModal, navbarUserExpanded, userData } = this.props

    const links = [
      <Item onClick={logout} style={styles.item} key={3} size={50} label='Logout' />
    ]

    return (
      <div className='navbar-wrapper' style={styles.main}>
        <div className='sidebar-toggle' onClick={toggleSidebar} style={styles.left}>
          { userData && <MenuIcon style={styles.menuIcon} />}
        </div>
        <div style={styles.right}>
          {!userData &&
            <Anchor onClick={toggleLoginModal}>Login</Anchor>
          }
          {userData && <UserButton
            links={links}
            expanded={navbarUserExpanded}
            toggle={toggleUser}
            name={userData.fullName} />
          }

        </div>
      </div>
    )
  }
}

Navbar.propTypes = {
  toggleSidebar: PropTypes.func.isRequired,
  toggleUser: PropTypes.func,
  navbarUserExpanded: PropTypes.bool,
  toggleLoginModal: PropTypes.func,
  logout: PropTypes.func,
  userData: PropTypes.object
}

export default Navbar
