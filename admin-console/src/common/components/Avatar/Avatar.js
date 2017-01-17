import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import styles from './avatar.styles'

@Radium
class Avatar extends Component {
  render () {
    const { image } = this.props
    const computedStyle = [
      styles.main,
      image && { backgroundImage: `url(${image})` }
    ]

    return (
      <div style={computedStyle} />
    )
  }
}

Avatar.propTypes = {
  image: PropTypes.string
}

export default Avatar
