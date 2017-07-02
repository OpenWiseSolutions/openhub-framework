import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import Modal from 'react-modal'
import { ValidForm } from 'valid-react-form'
import styles from './loginModal.styles'
import Anchor from '../Anchor/Anchor'
import Button from '../Button/Button'
import ModalHeader from '../ModalHeader/ModalHeader'
import Field from '../Field/Field'

@Radium
class LoginModal extends Component {
  render () {
    const { loginModalOpen, actions } = this.props
    return (
      <Modal style={styles} contentLabel='Login' isOpen={loginModalOpen} >
        <ModalHeader title={'Login'} onClose={actions.toggleLoginModal} />
        <ValidForm style={styles.form} onSubmit={actions.submitLogin} autoComplete='off' >
          <Field
            autoFocus
            required
            placeholder='Enter your username'
            name='username'
          />
          <Field
            required
            type='password'
            placeholder='Enter password'
            name='password' />
          <div style={styles.controls} >
            <Button style={styles.controls.submit} >Submit</Button>
            <Anchor style={styles.controls.cancel} onClick={actions.toggleLoginModal} >Cancel</Anchor>
          </div>
        </ValidForm>
      </Modal>
    )
  }
}

LoginModal.propTypes = {
  loginModalOpen: PropTypes.bool,
  actions: PropTypes.object
}

export default LoginModal
