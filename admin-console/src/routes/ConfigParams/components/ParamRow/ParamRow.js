import React, { PropTypes, Component } from 'react'
import Radium from 'radium'
import { pipe, omit, values, map, append, addIndex, toString } from 'ramda'
import styles from './paramRow.styles.js'

@Radium
class ParamRow extends Component {

  render () {
    const { data, openParam, count } = this.props

    const computedStyles = count % 2 === 0 ? styles.even : styles.odd

    const id = data.id

    const cells = pipe(
      omit(['id', 'categoryCode']),
      values,
      map((value) => typeof value !== 'string' ? toString(value) : value),
      addIndex(map)((cell, index) => <td style={styles.cell} key={index}>{cell}</td>),
      append(<td style={styles.button} key={id} onClick={() => openParam(id)}>edit</td>)
    )(data)

    return (
      <tr style={computedStyles}>
        {cells}
      </tr>
    )
  }
}

ParamRow.propTypes = {
  data: PropTypes.object,
  openParam: PropTypes.func,
  count: PropTypes.number
}

export default ParamRow
