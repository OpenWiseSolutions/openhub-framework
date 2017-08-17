/* eslint-disable react/jsx-no-bind */

import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import Dialog from 'react-md/lib/Dialogs'
import TextField from 'react-md/lib/TextFields'
import Button from 'react-md/lib/Buttons/Button'
import Radio from 'react-md/lib/SelectionControls/Radio'

@Radium
class NodeModal extends Component {
  constructor (props) {
    super(props)
    const { data } = this.props

    this.state = {
      name: data.name || '',
      description: data.description || '',
      state: data.state || ''
    }
  }

  handleSubmit (e) {
    e.preventDefault()
    const { data, updateNode } = this.props
    const { name, description, state } = this.state

    if (!name || !description) {
      return
    }

    const payload = {
      name,
      description,
      state
    }
    updateNode(data.id, payload, data)
  }

  render () {
    const { isOpen, close, data } = this.props
    return (
      <Dialog
        closeOnEsc
        onHide={close}
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
          <div style={{ marginLeft: '-15px' }} >
            <Radio
              id='run'
              name='run'
              value='RUN'
              label='Run'
              checked={this.state.state === 'RUN'}
              onChange={(state) => this.setState(() => ({ state }))}
            />
            <Radio
              id='existing'
              name='existing'
              value='HANDLES_EXISTING_MESSAGES'
              label='Handle existing messages'
              checked={this.state.state === 'HANDLES_EXISTING_MESSAGES'}
              onChange={(state) => this.setState(() => ({ state }))}
            />
            <Radio
              id='stopped'
              name='stopped'
              value='STOPPED'
              label='Stopped'
              checked={this.state.state === 'STOPPED'}
              onChange={(state) => this.setState(() => ({ state }))}
            />
          </div >
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
