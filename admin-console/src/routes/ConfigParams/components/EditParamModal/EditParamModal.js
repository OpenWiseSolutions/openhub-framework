import React, { Component } from 'react'
import PropTypes from 'prop-types'
import validator from 'validator'
import Radium from 'radium'
import Modal from 'react-modal'
import { ValidForm, Toggle } from 'valid-react-form'
import Field from '../../../../common/components/Field/Field'
import ModalHeader from '../../../../common/components/ModalHeader/ModalHeader'
import styles from './editParamModal.styles'
import Button from '../../../../common/components/Button/Button'
import Anchor from '../../../../common/components/Anchor/Anchor'

const Value = ({ type, value, label, name }) => {
  const wrap = (child) => (
    <div style={styles.row} >
      <div style={styles.label} >
        <span>{label}</span>
        <span style={{ color: 'red' }} >*</span>
      </div>
      {child}
    </div>
  )

  switch (type) {
    case 'BOOL':
      return wrap(<div><Toggle name={name} value={value} /></div>)
    case 'INT':
      return wrap(<Field required validator={{ err: (v) => validator.isInt(v) }} name={name} value={value} />)
    case 'FLOAT':
      return wrap(<Field required validator={{ err: (v) => validator.isFloat(v) }} name={name} value={value} />)
    case 'STRING':
    default:
      return wrap(<Field required name={name} value={value} />)
  }
}

@Radium
class EditParamModal extends Component {
  render () {
    const { isOpen, close, data, updateParam, updating, updateError } = this.props
    return (
      <Modal contentLabel='params' style={styles.main} isOpen={isOpen} >
        <ModalHeader onClose={close} title={'Edit Parameter'} />
        {isOpen && data &&
        <div style={styles.content} >
          {updateError && <div style={styles.error} >Update failed!</div>}
          <ValidForm autoComplete='off' onSubmit={(payload) => updateParam(data, payload)} >
            <div style={styles.row} >
              <div style={styles.label} >Code</div>
              <div>{data.code}</div>
            </div>
            <div style={styles.row} >
              <div style={styles.label} >Description</div>
              <div>{data.description}</div>
            </div>
            <br />
            <Value type={data.dataType} value={data.currentValue} label='Current Value' name='currentValue' />
            <Value type={data.dataType} value={data.defaultValue} label='Default Value' name='defaultValue' />
            <br />
            <div style={styles.row} >
              <div style={styles.label} >Validation</div>
              <div>
                <Field name='validationRegEx' value={data.validationRegEx} />
              </div>
            </div>
            <br />
            <div style={[styles.row, styles.controls]} >
              <Anchor style={styles.controls.cancel} onClick={close} >Cancel</Anchor>
              <Button style={styles.controls.submit} >{updating ? 'Updating...' : 'Update'}</Button>
            </div>
          </ValidForm>
        </div>
        }
      </Modal>
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
