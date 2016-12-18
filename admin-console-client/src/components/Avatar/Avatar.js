import React, { Component } from 'react'
import Radium from 'radium'
import styles from './avatar.styles'
// todo links! import { IndexLink, Link } from 'react-router'

@Radium
class Avatar extends Component {
  render () {
    return (
      <div style={styles.main} />
    )
  }
}

export default Avatar
