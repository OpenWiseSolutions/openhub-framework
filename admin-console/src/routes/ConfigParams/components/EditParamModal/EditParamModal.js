/* eslint-disable react/jsx-no-bind,react/prop-types */

import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { isEmpty } from 'ramda'
import validator from 'validator'
import Radium from 'radium'
import Dialog from 'react-md/lib/Dialogs'
import Flatpickr from 'react-flatpickr'
import TextField from 'react-md/lib/TextFields'
import Checkbox from 'react-md/lib/SelectionControls/Checkbox'
import Button from 'react-md/lib/Buttons/Button'
import styles from './editParamModal.styles'

const Value = ({ type, value, label, name, update, mandatory }) => {
  switch (type) {
    case 'BOOLEAN':
      return <Checkbox
        style={styles.checkbox}
        label={label}
        id={name}
        name={name}
        checked={value}
        value={value}
        onChange={(val) => update(name, val)}
      />
    case 'INT':
      return <TextField
        required={mandatory}
        label={label}
        name={name}
        id={name}
        value={value}
        onChange={(val) => update(name, val, (v) => validator.isInt(v))}
      />
    case 'FLOAT':
      return <TextField
        label={label}
        required={mandatory}
        name={name}
        id={name}
        value={value}
        onChange={(val) => update(name, val, (v) => validator.isFloat(v))}
      />
    case 'DATE':
      return <Flatpickr
        placeholder='Choose Date'
        style={styles.datepicker}
        value={value}
        onChange={(val) => update(name, val)}
      />
    case 'STRING':
    case 'FILE':
    default:
      return <TextField
        required={mandatory}
        label={label}
        name={name}
        id={name}
        value={value}
        onChange={(val) => update(name, val)}
      />
  }
}

@Radium
class EditParamModal extends Component {

  constructor (props) {
    super(props)
    this.state = {
      ...this.props.data
    }
  }

  handleSubmit (e) {
    e.preventDefault()
    const { data } = this.props
    const { currentValue, defaultValue, validationRegEx } = this.state

    if (data.mandatory) {
      const res = [currentValue, defaultValue]
        .filter((val) => !isEmpty(val))

      if (!res.length) {
        return
      }
    }

    const payload = {
      ...data,
      currentValue,
      defaultValue,
      validationRegEx
    }

    this.props.updateParam(payload)
  }

  update (field, value, validator) {
    if (validator && value && !validator(value)) {
      return
    }

    this.setState(() => ({
      [field]: value
    }))
  }

  render () {
    const { isOpen, close, data } = this.props
    return (
      <Dialog
        closeOnEsc
        id='modal'
        dialogClassName='md-dialog--big'
        title={'Edit Parameter'}
        visible={isOpen}
        onHide={close}
        actions={[
          <Button onClick={this.handleSubmit.bind(this)} raised label={'Update'} primary />,
          <Button raised label={'Cancel'} onClick={close} />
        ]}
      >
        {isOpen && data &&
        <form
          autoComplete='off'
          onSubmit={this.handleSubmit.bind(this)}
        >
          <TextField
            disabled
            label='Code'
            name='code'
            id='code'
            value={data.code}
          />
          <TextField
            disabled
            label='Description'
            name='description'
            id='description'
            value={data.description}
          />
          <TextField
            disabled
            label='Validation (reg. expr.)'
            name='validationRegEx'
            id='validationRegEx'
            value={data.validationRegEx}
          />
          <TextField
            disabled
            label='Data Type'
            name='datatype'
            id='datatype'
            value={data.dataType}
          />
          <Checkbox
            disabled
            style={styles.checkbox}
            label={'Mandatory'}
            id='mandatory'
            name='mandatory'
            checked={data.mandatory}
            value={data.mandatory}
          />
          <Value
            update={this.update.bind(this)}
            type={data.dataType}
            value={this.state.currentValue}
            mandatory={data.mandatory}
            label='Current Value'
            name='currentValue'
          />
          <Value
            update={this.update.bind(this)}
            type={data.dataType}
            mandatory={data.mandatory}
            value={this.state.defaultValue}
            label='Default Value'
            name='defaultValue'
          />
        </form >
        }
      </Dialog >
    )
  }
}

EditParamModal.propTypes = {
  isOpen: PropTypes.bool,
  updating: PropTypes.bool,
  updateError: PropTypes.bool,
  data: PropTypes.object,
  close: PropTypes.func,
  updateParam: PropTypes.func
}

export default EditParamModal
