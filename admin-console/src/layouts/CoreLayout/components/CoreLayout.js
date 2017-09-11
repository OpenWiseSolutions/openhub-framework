import React, { Component } from 'react'
import { isEmpty } from 'ramda'
import PropTypes from 'prop-types'
import Radium from 'radium'
import NavigationDrawer from 'react-md/lib/NavigationDrawers'
import LinearProgress from 'react-md/lib/Progress/LinearProgress'
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
      config,
      active
    } = this.props

    return (
      <NavigationDrawer
        ref={this._setContainer}
        visible={!!userData && sidebar}
        toolbarStyle={{ opacity: userData ? 1 : 0 }}
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
        <div style={{ width: '100%' }}>
          <LinearProgress
            id='progress'
            style={{
              margin: 0,
              marginTop: '-64px',
              marginBottom: '54px',
              width: '100%',
              height: '10px',
              opacity: !isEmpty(active) ? 1 : 0,
              zIndex: 20,
              backgroundColor: 'transparent'
            }}
          />
          {children}
        </div>
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
  title: PropTypes.string,
  active: PropTypes.object
}

export default CoreLayout
