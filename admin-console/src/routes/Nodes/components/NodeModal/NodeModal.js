/* eslint-disable react/jsx-no-bind */

import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import Dialog from 'react-md/lib/Dialogs'
import TextField from 'react-md/lib/TextFields'
import Button from 'react-md/lib/Buttons/Button'

@Radium
class NodeModal extends Component {
  constructor (props) {
    super(props)
    const { data } = this.props

    this.state = {
      name: data.name || '',
      description: data.description || ''
    }
  }

  handleSubmit (e) {
    e.preventDefault()
    const { data, updateNode } = this.props
    const { name, description } = this.state

    if (!name) {
      return
    }

    const payload = {
      name,
      description
    }
    updateNode(data.id, payload, data)
  }

  render () {
    const { isOpen, close, data } = this.props
    return (
      <Dialog
        closeOnEsc
        onHide={close}
        dialogClassName='md-dialog--big'
        title={'Cluster Node Update'}
        visible={isOpen}
        actions={[
          <Button onClick={this.handleSubmit.bind(this)} raised label={'Update'} primary />,
          <Button raised label={'Cancel'} onClick={close} />
        ]}
      >
        {isOpen && data &&
        <form autoComplete='off' onSubmit={this.handleSubmit.bind(this)} >
          <TextField
            label={'Name'}
            required
            value={this.state.name}
            onChange={(name) => this.setState(() => ({ name }))}
          />
          <TextField
            label={'Description'}
            value={this.state.description}
            onChange={(description) => this.setState(() => ({ description }))}
          />
          <button type='submit' hidden />
        </form >}
      </Dialog >
    )
  }
}

NodeModal.propTypes = {
  isOpen: PropTypes.bool,
  data: PropTypes.object,
  close: PropTypes.func,
  updateNode: PropTypes.func
}

export default NodeModal
