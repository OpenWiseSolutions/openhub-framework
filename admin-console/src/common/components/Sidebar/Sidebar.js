import React, { Component } from 'react'
import { path } from 'ramda'
import Radium from 'radium'
import styles from './sidebar.styles'
import Item from '../Item/Item'
import MdHome from 'react-icons/lib/md/home'
import MdVertDots from 'react-icons/lib/md/more-vert'
import MdMergeType from 'react-icons/lib/md/merge-type'
import MdInsertInvitation from 'react-icons/lib/md/insert-invitation'
import MdDeviceHub from 'react-icons/lib/md/device-hub'

@Radium
class Sidebar extends Component {
  render () {
    const { extended, config } = this.props
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
          {path(['configuration', 'systemParams', 'enable'], config) &&
          <Item link={'/config-params'}
            icon={<MdVertDots />}
            style={styles.nestedItem}
            label='Config parameters' />
          }
          {path(['configuration', 'logging', 'enable'], config) &&
          <Item link={'/config-logging'}
            icon={<MdVertDots />}
            style={styles.nestedItem}
            label='Config logging' />
          }
          {path(['configuration', 'environment', 'enable'], config) &&
          <Item link={'/environment-properties'}
            icon={<MdVertDots />}
            style={styles.nestedItem}
            label='Environment properties' />
          }
          {path(['configuration', 'errorCodeCatalog', 'enable'], config) &&
          <Item link={'/errors-overview'}
            icon={<MdVertDots />}
            style={styles.nestedItem}
            label='Errors Overview' />
          }
        </Item>
        {path(['changes', 'enable'], config) &&
        <Item link={'/changes'}
          icon={<MdMergeType />}
          style={styles.item}
          label='Changes' />}
        { path(['cluster', 'nodes', 'enable'], config) &&
        <Item link={'/nodes'}
          icon={<MdDeviceHub />}
          style={styles.item}
          label='Nodes' />}
      </div>
    )
  }
}

Sidebar.propTypes = {
  extended: React.PropTypes.bool,
  config: React.PropTypes.object
}

export default Sidebar
