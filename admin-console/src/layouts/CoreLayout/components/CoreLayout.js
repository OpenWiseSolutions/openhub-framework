import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import NavigationDrawer from 'react-md/lib/NavigationDrawers'
import styles from './coreLayout.styles'
import navItems from './navItems'

@Radium
class CoreLayout extends Component {

  constructor (props) {
    super(props)
    this.state = {
      toggle: true
    }
  }

  render () {
    const {
      userData,
      children,
      config
    } = this.props

    return (
      <NavigationDrawer
        ref={this._setContainer}
        visible={!!userData && this.state.toggle}
        onVisibilityToggle={() => this.setState(({ toggle }) => ({ toggle: !toggle }))}
        navItems={config && navItems(config.menu)}
        toolbarTitle={this.props.title}
        drawerTitle={<div style={styles.logoWrapper} >
          <div style={[styles.logoWrapper, styles.logo]} />
        </div >}
        defaultMedia={'desktop'}
        toolbarProminent={false}
        drawerType={NavigationDrawer.DrawerTypes.PERSISTENT}
      >
        {children}
      </NavigationDrawer>
    )
  }
}

CoreLayout.propTypes = {
  children: PropTypes.element.isRequired,
  sidebarExtended: PropTypes.bool,
  actions: PropTypes.object,
  authActions: PropTypes.object,
  navbarUserExpanded: PropTypes.bool,
  userData: PropTypes.object,
  config: PropTypes.object,
  title: PropTypes.string
}

export default CoreLayout
