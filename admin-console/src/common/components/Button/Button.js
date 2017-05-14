import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import styles from './button.styles'

@Radium
class Button extends Component {

  render () {
    const { style, fullWidth, children, ...other } = this.props
    const computedStyles = [
      styles,
      style,
      fullWidth && { width: '100%' }
    ]

    return (
      <button {...other} style={computedStyles}>
        {children}
      </button>
    )
  }

}

Button.propTypes = {
  children: PropTypes.node,
  fullWidth: PropTypes.bool,
  style: PropTypes.object
}

export default Button
