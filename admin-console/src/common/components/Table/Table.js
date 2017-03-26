import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import styles from './table.styles.js'

@Radium
class Table extends Component {

  render () {
    const { data } = this.props

    const columnStyle = [
      styles.column,
      data && { width: `${100 / data[0].length}%` }
    ]

    const column = (item, index) => {
      return <div key={`c${index}`} style={columnStyle} title={item}>{item}</div>
    }

    const row = (item, index) => {
      const rowStyle = [
        styles.row,
        index % 2 === 0 ? styles.even : styles.odd
      ]

      return <div key={`r${index}`} style={rowStyle}>{item.map(column)}</div>
    }

    const computedStyles = [
      styles.main
    ]

    return (
      <div style={computedStyles}>
        {data && data.map(row)}
      </div>
    )
  }
}

Table.propTypes = {
  data: PropTypes.array
}

export default Table
