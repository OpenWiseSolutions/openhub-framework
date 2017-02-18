import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import styles from './version.styles.js'

@Radium
class Footer extends Component {

  render () {
    const { version } = this.props

    const computedStyles = [
      styles.main
    ]

    return (
      <div style={computedStyles}>
        <span>{version}</span>
      </div>
    )
  }
}

Footer.propTypes = {
  version: PropTypes.string
}

export default Footer
