/* eslint-disable react/jsx-no-bind,react/prop-types */

import React, { Component } from 'react'
import PropTypes from 'prop-types'
import validator from 'validator'
import Radium from 'radium'
import Dialog from 'react-md/lib/Dialogs'
import Flatpickr from 'react-flatpickr'
import CardTitle from 'react-md/lib/Cards/CardTitle'
import TextField from 'react-md/lib/TextFields'
import Checkbox from 'react-md/lib/SelectionControls/Checkbox'
import Button from 'react-md/lib/Buttons/Button'
import styles from './editParamModal.styles'

const Value = ({ type, value, label, name, update }) => {
  switch (type) {
    case 'BOOL':
      return <Checkbox
        label={label}
        id={name}
        name={name}
        checked={value}
        value={value}
        onChange={(val) => update(name, val)}
      />
    case 'INT':
      return <TextField
        required
        label={label}
        name={name}
        id={name}
        value={value}
        onChange={(val) => update(name, val, (v) => validator.isInt(v))}
      />
    case 'FLOAT':
      return <TextField
        label={label}
        required
        name={name}
        id={name}
        value={value}
        onChange={(val) => update(name, val, (v) => validator.isFloat(v))}
      />
    case 'DATE':
      return <Flatpickr
        style={styles.datepicker}
        value={value}
        onChange={(val) => update(name, val)}
      />
    case 'STRING':
    case 'FILE':
    default:
      return <TextField
        required
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
    const { currentValue, defaultValue, validationRegEx } = this.state

    if (typeof currentValue !== Boolean && currentValue === '') {
      return
    }

    if (typeof defaultValue !== Boolean && defaultValue === '') {
      return
    }
    const payload = {
      ...this.props.data,
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
        modal
        id='modal'
        dialogClassName='md-dialog--big'
        title={'Edit Parameter'}
        visible={isOpen}
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
          <CardTitle
            style={{ paddingLeft: 0 }}
            title={data.code}
            subtitle={data.description}
          />
          <Value
            update={this.update.bind(this)}
            type={data.dataType}
            value={this.state.currentValue}
            label='Current Value'
            name='currentValue'
          />
          <Value
            update={this.update.bind(this)}
            type={data.dataType}
            value={this.state.defaultValue}
            label='Default Value'
            name='defaultValue'
          />
          <TextField
            label='Validation'
            name='validationRegEx'
            id='validationRegEx'
            value={this.state.validationRegEx}
            onChange={(val) => this.update('validationRegEx', val)}
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
