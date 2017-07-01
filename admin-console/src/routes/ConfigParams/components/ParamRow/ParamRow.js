import React, { PropTypes, Component } from 'react'
import Radium from 'radium'
import MdEdit from 'react-icons/lib/md/edit'
import { pipe, omit, values, map, append, addIndex, toString } from 'ramda'
import styles from './paramRow.styles.js'
import Button from '../../../../common/components/Button/Button'

@Radium
class ParamRow extends Component {

  render () {
    const { data, openParam, count } = this.props
    const computedStyles = count % 2 === 0 ? styles.even : styles.odd
    const id = data.id

    const ensureNumberOfCells = {
      code: '',
      currentValue: '',
      defaultValue: '',
      dataType: '',
      mandatory: '',
      description: '',
      validationRegEx: ''
    }

    const cells = pipe(
      omit(['id', 'categoryCode', 'dataType', 'mandatory', 'validationRegEx']),
      values,
      map((value) => typeof value !== 'string' ? toString(value) : value),
      addIndex(map)((cell, index) => <td style={styles.cell} key={index} >{cell}</td>),
      append(
        <td key={id} style={styles.cell} >
          <Button style={styles.button} fullWidth onClick={() => openParam(id)} >
            <MdEdit size={30} />
          </Button>
        </td>)
    )({ ...ensureNumberOfCells, ...data })

    return (
      <tr style={computedStyles} >
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
