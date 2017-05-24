import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import MdClose from 'react-icons/lib/md/close'
import styles from './modalHeader.styles'

@Radium
class ModalHeader extends Component {

  render () {
    const { onClose, title } = this.props

    const computedStyles = [
      styles.main
    ]

    return (
      <div style={computedStyles}>
        <div style={styles.title}>{title}</div>
        <div style={styles.close}>
          <MdClose onClick={onClose} />
        </div>
      </div>
    )
  }
}

ModalHeader.propTypes = {
  onClose: PropTypes.func,
  title: PropTypes.string
}

export default ModalHeader
