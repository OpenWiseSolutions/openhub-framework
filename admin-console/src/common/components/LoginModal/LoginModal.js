import React, { Component, PropTypes } from 'react'
import Radium from 'radium'
import Modal from 'react-modal'
import styles from './loginModal.styles'
import MdClose from 'react-icons/lib/md/close'
import Anchor from '../Anchor/Anchor'
import Button from '../Button/Button'
import { ValidForm, Field, ValidStyles } from 'valid-react-form'

@Radium
class LoginModal extends Component {
  render () {
    const { loginModalOpen, actions } = this.props
    return (
      <Modal style={styles} contentLabel='Login' isOpen={loginModalOpen}>
        <div style={styles.header}>
          <div style={styles.header.title}>Login</div>
          <div style={styles.header.close}>
            <MdClose onClick={actions.toggleLoginModal} />
          </div>
        </div>
        <ValidStyles>
          <ValidForm style={styles.form} onSubmit={actions.submitLogin} autoComplete='off'>
            <Field label='Username'
              required
              placeholder='Enter your username'
              name='username' />
            <Field label='Password'
              required
              type='password'
              placeholder='Enter password'
              name='password' />
            <div style={styles.controls}>
              <Button style={styles.controls.submit}>Submit</Button>
              <Anchor style={styles.controls.cancel} onClick={actions.toggleLoginModal}>Cancel</Anchor>
            </div>
          </ValidForm>
        </ValidStyles>
      </Modal>
    )
  }
}

LoginModal.propTypes = {
  loginModalOpen: PropTypes.bool,
  loginErrors: PropTypes.bool,
  actions: PropTypes.object
}

export default LoginModal
