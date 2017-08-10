import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import NavigationDrawer from 'react-md/lib/NavigationDrawers'
import styles from './coreLayout.styles'
import navItems from './navItems'
import ToolbarMenu from '../containers/toolbarMenu.container'

@Radium
class CoreLayout extends Component {
  render () {
    const {
      userData,
      children,
      sidebar,
      toggleSidebar,
      config
    } = this.props

    return (
      <NavigationDrawer
        ref={this._setContainer}
        visible={!!userData && sidebar}
        onVisibilityToggle={() => toggleSidebar(!sidebar)}
        navItems={config && navItems(config.menu)}
        toolbarTitle={this.props.title}
        drawerTitle={<div style={styles.logoWrapper} >
          <div style={[styles.logoWrapper, styles.logo]} />
        </div >}
        defaultMedia={'desktop'}
        toolbarProminent={false}
        drawerType={NavigationDrawer.DrawerTypes.PERSISTENT}
        toolbarActions={<ToolbarMenu />}
      >
        {children}
      </NavigationDrawer>
    )
  }
}

CoreLayout.propTypes = {
  children: PropTypes.element.isRequired,
  sidebar: PropTypes.bool,
  toggleSidebar: PropTypes.func,
  userData: PropTypes.object,
  config: PropTypes.object,
  title: PropTypes.string
}

export default CoreLayout
