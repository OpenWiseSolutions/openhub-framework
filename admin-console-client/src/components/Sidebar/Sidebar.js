import React, { Component } from 'react'
import Radium from 'radium'
import styles from './sidebar.styles'
// todo import { IndexLink, Link } from 'react-router'

@Radium
class Sidebar extends Component {
  render () {
    const { extended } = this.props
    const computedStyles = [
      styles.main,
      extended && styles.extended
    ]

    return (
      <div style={computedStyles} />
    )
  }
}

Sidebar.propTypes = {
  extended: React.PropTypes.bool
}

export default Sidebar
