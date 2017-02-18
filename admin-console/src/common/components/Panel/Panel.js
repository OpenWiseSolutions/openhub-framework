import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import styles from './panel.styles.js'

@Radium
class Panel extends Component {

  render () {
    const { title, children, style } = this.props

    const computedStyles = [
      styles.main,
      style
    ]

    return (
      <div style={computedStyles} >
        {title && <div style={styles.title}>{title}</div>}
        <div style={styles.content}>
          { children }
        </div>
      </div>
    )
  }
}

Panel.propTypes = {
  title: PropTypes.string,
  children: PropTypes.element,
  style: PropTypes.object
}

export default Panel
