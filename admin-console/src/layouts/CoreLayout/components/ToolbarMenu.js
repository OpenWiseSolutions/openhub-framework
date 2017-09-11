import React, { Component } from 'react'
import PropTypes from 'prop-types'
import List from 'react-md/lib/Lists/List'
import ListItem from 'react-md/lib/Lists/ListItem'
import MenuButton from 'react-md/lib/Menus/MenuButton'
import Menu from 'react-md/lib/Menus/Menu'

class ToolbarMenu extends Component {
  render () {
    const { userData, logoutUser } = this.props
    if (!userData) return null
    const { fullName } = userData
    return (
      <div style={{ height: '100%', display: 'flex', justifyContent: 'center', flexDirection: 'column' }} >
        <MenuButton flat position={Menu.Positions.BELOW} label={fullName} id='static-1' >
          <List>
            <ListItem onClick={logoutUser} primaryText={'Logout'} />
          </List >
        </MenuButton >
      </div >
    )
  }
}

ToolbarMenu.propTypes = {
  userData: PropTypes.object,
  logoutUser: PropTypes.func
}

export default ToolbarMenu
