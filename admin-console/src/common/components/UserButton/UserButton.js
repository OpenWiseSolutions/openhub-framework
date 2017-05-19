import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import ArrowDown from 'react-icons/lib/md/keyboard-arrow-down'
import styles from './userButton.styles'

@Radium
class UserButton extends Component {
  render () {
    const { avatar, name, expanded, toggle, links } = this.props
    return (
      <div onClick={toggle} style={styles.main}>
        {avatar}
        <span style={styles.name}>{name}</span>
        { links && <ArrowDown style={styles.arrow} /> }
        { expanded && links && <div className='menu' style={styles.menu}>{links}</div> }
      </div>
    )
  }
}

UserButton.propTypes = {
  name: PropTypes.string,
  expanded: PropTypes.bool,
  toggle: PropTypes.func,
  links: PropTypes.node,
  avatar: PropTypes.node
}

export default UserButton
