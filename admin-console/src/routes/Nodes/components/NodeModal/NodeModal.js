import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import Modal from 'react-modal'
import { ValidForm } from 'valid-react-form'
import Field from '../../../../common/components/Field/Field'
import ModalHeader from '../../../../common/components/ModalHeader/ModalHeader'
import styles from './nodeModal.styles'
import Button from '../../../../common/components/Button/Button'

@Radium
class NodeModal extends Component {
  constructor (props) {
    super(props)
    this.state = {
      state: this.props.data.state
    }
  }

  changeState (state) {
    this.setState(() => ({
      state
    }))
  }

  render () {
    const { isOpen, updateNode, close, data } = this.props
    const { state } = this.state
    return (
      <Modal contentLabel='params' style={styles.main} isOpen={isOpen}>
        <ModalHeader onClose={close} title={'Cluster Node Update'} />
        {isOpen && data &&
        <div style={styles.content}>
          <ValidForm extended={{ state }} autoComplete='off' onSubmit={(payload) => updateNode(data.id, payload)}>
            <div style={styles.row}>
              <div style={styles.label}>Name</div>
              <div style={styles.field}>
                <Field required name='name' value={data.name} />
              </div>
            </div>
            <div style={styles.row}>
              <div style={styles.label}>Description</div>
              <div style={styles.field}>
                <Field name='description' value={data.description} />
              </div>
            </div>
            <div style={styles.row}>
              <div style={styles.label}>State</div>
              <div style={styles.state} onChange={(event) => this.changeState(event.target.value)}>
                  Run
                  <input
                    defaultChecked={state === 'RUN'}
                    style={styles.radio}
                    value='RUN'
                    type='radio'
                    name='state'
                  /> <br />
                  Handle existing messages
                  <input
                    defaultChecked={state === 'HANDLES_EXISTING_MESSAGES'}
                    style={styles.radio}
                    type='radio'
                    value='HANDLES_EXISTING_MESSAGES'
                    name='state'
                  /><br />
                  Stopped
                  <input
                    defaultChecked={state === 'STOPPED'}
                    style={styles.radio}
                    value='STOPPED'
                    type='radio'
                    name='state'
                  />
              </div>
            </div>
            <div style={[styles.row, styles.controls]}>
              <Button style={styles.controls.submit} primary>{'Update'}</Button>
              <Button style={styles.controls.cancel} onClick={close}>Cancel</Button>
            </div>
          </ValidForm>
        </div>}
      </Modal>
    )
  }
}

NodeModal.propTypes = {
  isOpen    : PropTypes.bool,
  data      : PropTypes.object,
  close     : PropTypes.func,
  updateNode: PropTypes.func
}

export default NodeModal
