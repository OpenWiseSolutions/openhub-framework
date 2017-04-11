import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import styles from './navbar.styles'
import MenuIcon from 'react-icons/lib/md/menu'
import UserButton from '../UserButton/UserButton'
import Item from '../Item/Item'
import Avatar from '../Avatar/Avatar'
import Anchor from '../Anchor/Anchor'

@Radium
class Navbar extends Component {
  render () {
    const { logout, toggleSidebar, toggleUser, toggleLoginModal, navbarUserExpanded, authUser } = this.props

    const links = [
      <Item style={styles.item} key={1} size={50} label='Account' />,
      <Item style={styles.item} key={2} size={50} label='Settings' />,
      <Item onClick={logout} style={styles.item} key={3} size={50} label='Logout' />
    ]

    return (
      <div className='navbar-wrapper' style={styles.main}>
        <div className='sidebar-toggle' onClick={toggleSidebar} style={styles.left}>
          { authUser && <MenuIcon style={styles.menuIcon} />}
        </div>
        <div style={styles.right}>
          {!authUser &&
            <Anchor onClick={toggleLoginModal}>Login</Anchor>
          }
          {authUser && <UserButton avatar={<Avatar />}
            links={links}
            expanded={navbarUserExpanded}
            toggle={toggleUser}
            name={authUser.fullName} />
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
  authUser: PropTypes.object
}

export default Navbar
