import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import ArrowDown from 'react-icons/lib/md/keyboard-arrow-down'
import styles from './userButton.styles'
import Avatar from '../Avatar/Avatar'
// todo links! import { IndexLink, Link } from 'react-router'

@Radium
class UserButton extends Component {
  render () {
    const { name, expanded, toggle } = this.props
    return (
      <div onClick={toggle} style={styles.main}>
        <Avatar />
        <span style={styles.name}>{name}</span>
        <ArrowDown style={styles.arrow} />
        { expanded && <div className='menu' style={styles.menu} /> }
      </div>
    )
  }
}

UserButton.propTypes = {
  name: PropTypes.string,
  expanded: PropTypes.bool,
  toggle: PropTypes.func
}

export default UserButton
