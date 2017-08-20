import React, { Component } from 'react'
import { isEmpty } from 'ramda'
import PropTypes from 'prop-types'
import Radium from 'radium'
import Card from 'react-md/lib/Cards/Card'
import CardTitle from 'react-md/lib/Cards/CardTitle'
import CardText from 'react-md/lib/Cards/CardText'
import CardActions from 'react-md/lib/Cards/CardActions'
import Button from 'react-md/lib/Buttons/Button'
import TextField from 'react-md/lib/TextFields'
import styles from './loginCard.styles'

@Radium
class LoginModal extends Component {
  constructor (props) {
    super(props)

    this.state = {
      username: '',
      password: '',
      errors: {}
    }
  }

  handleSubmit (e) {
    e.preventDefault()
    const { username, password } = this.state
    const errors = {}

    if (!username) {
      errors.username = true
    }
    if (!password) {
      errors.password = true
    }

    if (isEmpty(errors)) {
      return this.props.submitLogin({ username, password })
    }

    this.setState(() => ({ errors }))
  }

  render () {
    const { name, version } = this.props
    return (
      <div style={styles.card} >
        <div style={styles.logo} />
        <Card className='md-block-centered' >
          <form onSubmit={(e) => this.handleSubmit(e)} >
            <CardText >
              <TextField
                id='username'
                value={this.state.username}
                onChange={(username) => this.setState(() => ({ username }))}
                error={this.state.errors.username}
                errorText='This is required!'
                label='username'
                placeholder='username'
              />
              <TextField
                id='password'
                value={this.state.password}
                onChange={(password) => this.setState(() => ({ password }))}
                error={this.state.errors.password}
                errorText='This is required!'
                label='password'
                type='password'
              />
            </CardText >
            <CardActions className='md-divider-border md-divider-border--top md-text-right' >
              <Button type='submit' className='md-full-width' label='Login' primary raised />
            </CardActions >
          </form >
        </Card >
        <div style={{ marginTop: '10px' }} >
          <div className='md-grid'>
            <div className='md-cell md-cell--6'>{name}</div>
            <div className='md-cell md-cell--6 md-text-right'>{version}</div>
          </div>
        </div >
      </div >
    )
  }
}

LoginModal.propTypes = {
  submitLogin: PropTypes.func,
  name: PropTypes.string,
  version: PropTypes.string
}

export default LoginModal
