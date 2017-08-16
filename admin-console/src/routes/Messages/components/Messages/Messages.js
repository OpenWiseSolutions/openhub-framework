import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import { ValidForm, Field, Valid, Select } from 'valid-react-form'
import { hashHistory } from 'react-router'
import moment from 'moment'
import Flatpickr from 'react-flatpickr'
import Panel from '../../../../common/components/Panel/Panel'
import Button from '../../../../common/components/Button/Button'
import Row from '../../../../common/components/Row/Row'
import styles from './messages.styles.js'

const states = [
  { label: 'OK', value: 'OK' },
  { label: 'FAILED', value: 'FAILED' },
  { label: 'CANCEL', value: 'CANCEL' }
]

@Radium
class Messages extends Component {
  constructor (props) {
    super(props)
    this.state = {
      form: true
    }
  }

  componentDidMount () {
    this.props.getMessages()
  }

  reset () {
    this.setState(() => ({ form: false }))
    setTimeout(() => {
      this.setState(() => ({ form: true }))
    }, 0)
  }

  openDetail ({ id }) {
    hashHistory.push(`/messages/${id}`)
  }

  render () {
    const { messages } = this.props
    const validStyles = this.context.styles
    const datepickerStyle = { ...validStyles.field, ...styles.datepicker }
    return (
      <Panel style={styles.panel} title={'Messages'} >
        {this.state.form &&
        <ValidForm onSubmit={this.props.getMessages}>
          <div style={styles.formContent} >
            <div style={styles.column} >
              <Row label={'Received FROM'} >
                <Valid style={datepickerStyle} name={'receivedFrom'} >
                  <Flatpickr defaultValue={moment().subtract(1, 'minute').format()} data-enable-time />
                </Valid>
              </Row>
              <Row label={'Last Change FROM'} >
                <Valid style={datepickerStyle} name={'lastChangeFrom'} >
                  <Flatpickr data-enable-time />
                </Valid>
              </Row>
              <Row label={'Source System'} >
                <Field name={'sourceSystem'} />
              </Row>
              <Row label={'Process ID'} >
                <Field name={'processId'} />
              </Row>
              <Row label={'Error Code'} >
                <Field name={'errorCode'} />
              </Row>
              <Row label={'Operation Name'} >
                <Field name={'operationName'} />
              </Row>
            </div>
            <div style={styles.column} >
              <Row label={'Received TO'} >
                <Valid style={datepickerStyle} name={'receivedTo'} >
                  <Flatpickr data-enable-time />
                </Valid>
              </Row>
              <Row label={'Last Change TO'} >
                <Valid style={datepickerStyle} name={'lastChangeTo'} >
                  <Flatpickr data-enable-time />
                </Valid>
              </Row>
              <Row label={'Correlation ID'} >
                <Field name={'correlationId'} />
              </Row>
              <Row label={'State'} >
                <Select data={states} name={'state'} />
              </Row>
              <Row label={'Service Name'} >
                <Field name={'serviceName'} />
              </Row>
              <Row label={'Fulltext'} >
                <Field name={'fulltext'} />
              </Row>
            </div>
          </div>
          <div style={styles.controls} >
            <Button style={styles.control}>Submit</Button>
            <Button style={styles.control} onClick={() => this.reset()} type='button' >Reset</Button>
          </div >
        </ValidForm>}
        {messages.length && <table style={[styles.table, styles.messages]}>
          <tbody>
            <tr>
              <th style={styles.header}>{'Correlation ID'}</th>
              <th style={styles.header}>{'Source System'}</th>
              <th style={styles.header}>{'Received Time'}</th>
              <th style={styles.header}>{'Start Process Time'}</th>
              <th style={styles.header}>{'State'}</th>
              <th style={styles.header}>{'Error Code'}</th>
              <th style={styles.header}>{'Service'}</th>
              <th style={styles.header}>{'Operation'}</th>
            </tr>
            {messages.map((message, index) => (
              <tr
                onClick={() => this.openDetail(message)}
                key={message.id}
                style={index % 2 === 0 ? styles.even : styles.odd}
              >
                <td style={styles.cell}>{message.correlationId}</td>
                <td style={styles.cell}>{message.sourceSystem}</td>
                <td style={styles.cell}>{moment(message.received).format('MMMM Do YYYY, hh:mm:ss')}</td>
                <td style={styles.cell}>{moment(message.processingStarted).format('MMMM Do YYYY, hh:mm:ss')}</td>
                <td style={styles.cell}>{message.state}</td>
                <td style={styles.cell}>{message.errorCode}</td>
                <td style={styles.cell}>{message.serviceName}</td>
                <td style={styles.cell}>{message.operationName}</td>
              </tr>
            ))}
          </tbody>
        </table>}
      </Panel>
    )
  }
}

Messages.contextTypes = {
  styles: PropTypes.object
}

Messages.propTypes = {
  messages: PropTypes.array,
  getMessages: PropTypes.func
}

export default Messages
