import React, { Component } from 'react'
import Radium from 'radium'
import styles from './sidebar.styles'
import Item from '../Item/Item'
import MdHome from 'react-icons/lib/md/home'
import MdVertDots from 'react-icons/lib/md/more-vert'
import MdMergeType from 'react-icons/lib/md/merge-type'
import MdAnnouncement from 'react-icons/lib/md/announcement'
import MdInsertInvitation from 'react-icons/lib/md/insert-invitation'
import MdDeviceHub from 'react-icons/lib/md/device-hub'

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
          <Item link={'/config-logging'}
            icon={<MdVertDots />}
            style={styles.nestedItem}
            label='Config logging' />
          <Item link={'/environment-properties'}
            icon={<MdVertDots />}
            style={styles.nestedItem}
            label='Environment properties' />
        </Item>
        <Item link={'/changes'}
          icon={<MdMergeType />}
          style={styles.item}
          label='Changes' />
        <Item link={'/errors-overview'}
          icon={<MdAnnouncement />}
          style={styles.item}
          label='Errors Overview' />
        <Item link={'/nodes'}
          icon={<MdDeviceHub />}
          style={styles.item}
          label='Nodes' />
      </div>
    )
  }
}

Sidebar.propTypes = {
  extended: React.PropTypes.bool
}

export default Sidebar
