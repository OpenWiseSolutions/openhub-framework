import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import styles from './row.styles.js'

@Radium
class Row extends Component {

  render () {
    const { label, children } = this.props
    return (
      <div style={styles.row} >
        {label && <div style={styles.label}>{label}</div>}
        <div style={styles.children}>{children}</div>
      </div>
    )
  }
}

Row.propTypes = {
  label: PropTypes.string,
  children: PropTypes.element
}

export default Row
