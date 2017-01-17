import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import styles from './anchor.styles'

@Radium
class Anchor extends Component {

  render () {
    const { children, style, ...other } = this.props
    return (
      <span {...other} style={[styles, style]}>
        {children}
      </span>
    )
  }

}

Anchor.propTypes = {
  children: PropTypes.node,
  style: PropTypes.object
}

export default Anchor
