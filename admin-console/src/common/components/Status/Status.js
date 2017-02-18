import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import styles from './status.styles.js'

@Radium
class Status extends Component {

  render () {
    const { status } = this.props

    const computedStyles = [
      styles.main
    ]

    return (
      <div style={computedStyles} >
        {status && <span style={styles.up}> UP </span>}
        {!status && <span style={styles.down}> DOWN </span>}
      </div>
    )
  }
}

Status.propTypes = {
  status: PropTypes.bool.isRequired
}

export default Status
