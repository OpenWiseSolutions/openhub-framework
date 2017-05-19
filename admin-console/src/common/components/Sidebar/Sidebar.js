import React, { Component } from 'react'
import { path } from 'ramda'
import Radium from 'radium'
import styles from './sidebar.styles'
import Item from '../Item/Item'
import MdHome from 'react-icons/lib/md/home'
import MdDrafts from 'react-icons/lib/md/drafts'
import MdMergeType from 'react-icons/lib/md/merge-type'
import MdBuild from 'react-icons/lib/md/build'
import MdBlurLinear from 'react-icons/lib/md/blur-linear'
import MdDeviceHub from 'react-icons/lib/md/device-hub'
import MdBugReport from 'react-icons/lib/md/bug-report'
import MdExtension from 'react-icons/lib/md/extension'
import MdInfo from 'react-icons/lib/md/info'
import MdFilterHdr from 'react-icons/lib/md/filter-hdr'
import MdDeveloperBoard from 'react-icons/lib/md/developer-board'
import MdNotificationsActive from 'react-icons/lib/md/notifications-active'
import MdLink from 'react-icons/lib/md/link'
import MdKeyboardArrowRight from 'react-icons/lib/md/keyboard-arrow-right'

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
        <Item
          link={'/'} icon={<MdHome />}
          style={styles.item}
          expandedStyle={styles.item.expanded}
          label='Dashboard'
        />

        {/* Analytics */}
        {config.analytics && <Item
          icon={<MdBlurLinear />}
          style={styles.item}
          expandedStyle={styles.item.expanded}
          label='Analytics'
        >
          {path(['analytics', 'messages', 'enable'], config) &&
          <Item
            link={'/messages'}
            icon={<MdDrafts />}
            style={styles.nestedItem}
            label='Messages'
          />}
        </Item>}

        {/* Infrasctructure */}
        {config.cluster && <Item
          icon={<MdDeveloperBoard />}
          style={styles.item}
          expandedStyle={styles.item.expanded}
          label='Infrastructure'
        >
          { path(['cluster', 'nodes', 'enable'], config) &&
          <Item
            link={'/nodes'}
            icon={<MdDeviceHub />}
            style={styles.nestedItem}
            label='Nodes'
          />}
        </Item>}

        {/* Configuration */}
        <Item
          icon={<MdBuild />}
          style={styles.item}
          expandedStyle={styles.item.expanded}
          label='Configuration'
        >
          {path(['configuration', 'systemParams', 'enable'], config) &&
          <Item link={'/config-params'}
            icon={<MdExtension />}
            style={styles.nestedItem}
            label='Config parameters' />
          }
          {path(['configuration', 'logging', 'enable'], config) &&
          <Item link={'/config-logging'}
            icon={<MdInfo />}
            style={styles.nestedItem}
            label='Config logging' />
          }
          {path(['configuration', 'environment', 'enable'], config) &&
          <Item link={'/environment-properties'}
            icon={<MdFilterHdr />}
            style={styles.nestedItem}
            label='Environment properties' />
          }
          {path(['configuration', 'alerts', 'enable'], config) &&
          <Item link={'/alerts'}
            icon={<MdNotificationsActive />}
            style={styles.nestedItem}
            label='Alerts' />
          }
          {path(['configuration', 'errorCodeCatalog', 'enable'], config) &&
          <Item link={'/errors-overview'}
            icon={<MdBugReport />}
            style={styles.nestedItem}
            label='Errors Overview' />
          }
        </Item>

        {/* External links */}
        { path(['externalLinks', 'enable'], config) &&
        <Item
          icon={<MdLink />}
          style={styles.item}
          expandedStyle={styles.item.expanded}
          label='External links'
         >
          {config.externalLinks.items.map(({ title, link }) => (
            <Item
              key={link}
              externalLink={link}
              icon={<MdKeyboardArrowRight />}
              style={styles.nestedItem}
              label={title}
            />))}
        </Item>}

        {/* Changes */}
        {path(['changes', 'enable'], config) &&
        <Item
          link={'/changes'}
          icon={<MdMergeType />}
          style={styles.item}
          label='Changes'
        />}
      </div>
    )
  }
}

Sidebar.propTypes = {
  extended: React.PropTypes.bool,
  config: React.PropTypes.object
}

export default Sidebar
