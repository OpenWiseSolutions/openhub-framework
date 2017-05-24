import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import { toLower } from 'ramda'
import styles from './loggerRow.styles.js'

@Radium
class LoggerRow extends Component {

  render () {
    const { label, levels, configuredLevel, updateLogger } = this.props

    const buttonStyle = (level) => [
      styles.button,
      level === configuredLevel && styles[toLower(level)]
    ]

    return (
      <div style={styles.main} >
        <div style={styles.label}>{label}</div>
        <div style={styles.controls} >
          {levels.map(l => <div
            key={l}
            onClick={() => updateLogger(label, l)}
            style={buttonStyle(l)}>{l}</div>)}
        </div>
      </div>
    )
  }
}

LoggerRow.propTypes = {
  label: PropTypes.string,
  updateLogger: PropTypes.func,
  configuredLevel: PropTypes.string,
  levels: PropTypes.array
}

export default LoggerRow
