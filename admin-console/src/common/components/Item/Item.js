import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import ArrowDown from 'react-icons/lib/md/keyboard-arrow-down'
import styles from './item.styles'

@Radium
class Item extends Component {

  constructor (props) {
    super(props)
    this.handleClick = this.handleClick.bind(this)
    this.state = {
      expanded: false
    }
  }

  handleClick (event) {
    const { link, onClick, children } = this.props
    const { router } = this.context
    event && event.stopPropagation()
    !children && onClick && onClick()
    link && router.push(link)
    children && this.setState(({ expanded }) => ({ expanded: !expanded }))
  }

  render () {
    const { children, label, size, icon, style = {}, expandedStyle = {} } = this.props
    const { expanded } = this.state

    const computedStyle = [
      styles.main,
      size && { lineHeight: `${size}px`, minHeight: `${size}px` },
      style
    ]

    const labelStyle = [
      styles.label,
      expanded && expandedStyle
    ]

    return (
      <div className='item' onClick={this.handleClick} style={computedStyle}>
        <div style={labelStyle}>
          { icon && <span style={styles.icon}>{icon}</span> }
          <span>{label}</span>
          { children && <div style={styles.arrow}><ArrowDown /></div> }
        </div>
        { children && expanded && <div style={styles.children}>{children}</div> }
      </div>
    )
  }
}

Item.propTypes = {
  children: PropTypes.node,
  icon: PropTypes.node,
  label: PropTypes.string,
  size: PropTypes.number,
  link: PropTypes.string,
  onClick: PropTypes.func,
  style: PropTypes.object,
  expandedStyle: PropTypes.object
}

Item.contextTypes = {
  router: PropTypes.any
}

export default Item
