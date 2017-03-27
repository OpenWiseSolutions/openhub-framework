import React, { Component } from 'react'
import Radium from 'radium'
import styles from './sidebar.styles'
import Item from '../Item/Item'
import MdHome from 'react-icons/lib/md/home'
import MdVertDots from 'react-icons/lib/md/more-vert'
import MdInsertInvitation from 'react-icons/lib/md/insert-invitation'

@Radium
class Sidebar extends Component {
  render () {
    const { extended } = this.props
    const computedStyles = [
      styles.main,
      extended && styles.extended
    ]

    return (
      <div style={computedStyles}>
        <div style={styles.logo} />
        <Item link={'/'} icon={<MdHome />}
          style={styles.item}
          expandedStyle={styles.item.expanded}
          label='Dashboard' />
        <Item icon={<MdInsertInvitation />}
          style={styles.item}
          expandedStyle={styles.item.expanded}
          label='Configuration'>
          <Item link={'/config-params'}
            icon={<MdVertDots />}
            style={styles.nestedItem}
            label='Config parameters' />
        </Item>
      </div>
    )
  }
}

Sidebar.propTypes = {
  extended: React.PropTypes.bool
}

export default Sidebar
