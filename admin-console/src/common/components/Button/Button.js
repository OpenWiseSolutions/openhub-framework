import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import styles from './button.styles'

@Radium
class Button extends Component {

  render () {
    const { style, children, ...other } = this.props
    const computedStyles = [
      styles,
      style
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
  style: PropTypes.object
}

export default Button
