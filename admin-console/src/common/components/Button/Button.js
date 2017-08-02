import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import styles from './button.styles'
import { componentsColor } from '../../../styles/colors'

@Radium
class Button extends Component {

  render () {
    const { style, primary, link, fullWidth, children, ...other } = this.props
    const color = primary && componentsColor
    const computedStyles = [
      styles(color),
      style,
      link && { backgroundColor: 'transparent' },
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
  style: PropTypes.object,
  link: PropTypes.bool,
  primary: PropTypes.bool
}

export default Button
