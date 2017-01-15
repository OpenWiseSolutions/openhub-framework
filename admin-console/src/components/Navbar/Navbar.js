import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import styles from './navbar.styles'
import MenuIcon from 'react-icons/lib/md/menu'
import UserButton from '../UserButton/UserButton'
import Item from '../Item/Item'
import Avatar from '../Avatar/Avatar'

// todo links import { IndexLink, Link } from 'react-router'

@Radium
class Navbar extends Component {
  render () {
    const { toggleSidebar, toggleUser, navbarUserExpanded } = this.props

    // todo tomas >> api for links || config api?
    // todo tomas >> api for user info && avatar image
    // todo dynamically generate/map links
    const links = [
      <Item style={styles.item} key={1} size={50} label='Random Label 1' />,
      <Item style={styles.item} key={2} size={50} label='Random Label 2' />,
      <Item style={styles.item} key={3} size={50} label='Random Label 3' />
    ]

    return (
      <div className='navbar-wrapper' style={styles.main}>
        <div className='sidebar-toggle' onClick={toggleSidebar} style={styles.left}>
          <MenuIcon style={styles.menuIcon} />
        </div>
        <div style={styles.right}>
          <UserButton avatar={<Avatar />}
            links={links}
            expanded={navbarUserExpanded}
            toggle={toggleUser}
            name='Tomas Hanus' />
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
