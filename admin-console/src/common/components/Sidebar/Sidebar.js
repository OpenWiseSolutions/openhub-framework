import React, { Component } from 'react'
import Radium from 'radium'
import styles from './sidebar.styles'
import Item from '../Item/Item'
import MdHome from 'react-icons/lib/md/home'
import MdImportantDevices from 'react-icons/lib/md/important-devices'
import MdChart from 'react-icons/lib/md/insert-chart'
import MdVertDots from 'react-icons/lib/md/more-vert'
import MdInsertInvitation from 'react-icons/lib/md/insert-invitation'
import MdLanguage from 'react-icons/lib/md/language'

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
        <Item icon={<MdHome />} style={styles.item} expandedStyle={styles.item.expanded} label='Item 1' />
        <Item icon={<MdImportantDevices />} style={styles.item} label='Item 2' />
        <Item icon={<MdChart />} style={styles.item} label='Item 3' />
        <Item icon={<MdInsertInvitation />} style={styles.item} expandedStyle={styles.item.expanded} label='Item 4'>
          <Item icon={<MdVertDots />} style={styles.nestedItem} label='Nested item' />
        </Item>
        <Item icon={<MdLanguage />} style={styles.item} label='Item 5' />
      </div>
    )
  }
}

Sidebar.propTypes = {
  extended: React.PropTypes.bool
}

export default Sidebar
