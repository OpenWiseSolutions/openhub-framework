import React, { Component } from 'react'
import PropTypes from 'prop-types'
import moment from 'moment'
import Radium from 'radium'
import SyntaxHighlighter, { registerLanguage } from 'react-syntax-highlighter/dist/light'
import xml from 'react-syntax-highlighter/dist/languages/xml'
import xcode from 'react-syntax-highlighter/dist/styles/xcode'
import styles from './message.styles'
import Panel from '../../../../common/components/Panel/Panel'
import Button from '../../../../common/components/Button/Button'

registerLanguage('xml', xml)

const codeStyle = { ...xcode, ...styles.code }

@Radium
class Message extends Component {
  constructor (props) {
    super(props)
    this.state = {
      totalCheckbox: false
    }
  }

  componentDidMount () {
    const { id } = this.props.params
    this.props.getMessage(id)
  }

  toggle () {
    this.setState(({ totalCheckbox }) => ({
      totalCheckbox: !totalCheckbox
    }))
  }

  render () {
    const { message, restart, cancel } = this.props
    const states = ['OK', 'FAILED', 'CANCEL']
    if (!message) return <div>Loading...</div>

    const rows = [
      { t: 'Msg ID', v: message.id },
      { t: 'Correlation ID', v: message.correlationId },
      { t: 'Process ID', v: message.processId },
      { t: 'State of message processing', v: message.state },
      { t: 'The time when the process began processing', v: message.processingStarted },
      { t: 'The time of latest change', v: message.lastChange },
      { t: 'Error code', v: message.errorCode },
      { t: 'Failed count', v: message.failedCount },
      { t: 'Source system', v: message.sourceSystem },
      { t: 'The time when message was received', v: message.received },
      { t: 'The time in message', v: message.msgTimestamp },
      { t: 'Service name', v: message.serviceName },
      { t: 'Operation name', v: message.operationName },
      { t: 'ID of the object to be changed', v: message.objectId },
      { t: 'Type of the entity to be changed', v: message.entityType },
      { t: 'Funnel value', v: message.funnelValue },
      { t: 'ID of funnel component', v: message.funnelComponentId },
      { t: 'Guaranteed order', v: message.guaranteedOrder },
      { t: 'Exclude failed state', v: message.excludeFailedState },
      { t: 'Business error overview', v: message.businessError },
      { t: 'ID of parent message', v: message.parentMsgId },
      { t: 'Content (body) of message', v: <SyntaxHighlighter style={codeStyle} >{message.body}</SyntaxHighlighter> },
      { t: 'Whole incoming message', v: <SyntaxHighlighter style={codeStyle} >{message.envelope}</SyntaxHighlighter> },
      { t: 'Error description', v: message.failedDescription }
    ].map((item) => {
      if (typeof item.v === 'boolean') {
        item.v = item.v ? 'YES' : 'NO'
      }
      if (item.v === null || item.v === undefined) {
        item.v = '-'
      }
      return item
    })

    return (
      <div style={styles.main} >
        <Panel style={styles.panel} title={'Message Details'} >
          <table style={styles.table} >
            <tbody>
              <tr>
                <th style={styles.header} >{'Attribute Name'}</th>
                <th style={styles.header} >{'Attribute Value'}</th>
              </tr>
              {rows.map((row, index) => (
                <tr key={row.t} style={index % 2 === 0 ? styles.even : styles.odd} >
                  <td style={[styles.cell, styles.smallCell]} >{row.t}</td>
                  <td style={[styles.cell, { width: 'auto' }]} >{row.v}</td>
                </tr>
            ))}
            </tbody>
          </table>
          <div style={styles.controls} >
            <div>
              <input
                value='total'
                disabled={!states.includes(message.state)}
                type='checkbox'
                name='total'
                onChange={() => this.toggle()}
              /> total
            </div>
            <Button
              disabled={!states.includes(message.state)}
              type='button'
              primary
              onClick={() => restart(message.id, this.state.totalCheckbox)}
            >
              Restart
            </Button >
            <Button
              disabled={states.includes(message.state)}
              type='button'
              onClick={() => cancel(message.id)}
            >Cancel
            </Button >
          </div>
        </Panel>
        <Panel style={styles.panel} title={'List of external calls'} >
          <table style={styles.table}>
            <tbody>
              <tr>
                <th style={styles.header} >{'Internal ID'}</th>
                <th style={styles.header} >{'State'}</th>
                <th style={styles.header} >{'Operation name'}</th>
                <th style={styles.header} >{'ID of call'}</th>
                <th style={styles.header} >{'Last change time'}</th>
              </tr>
              {message.externalCalls.map((call, index) => (
                <tr key={call.id} style={index % 2 === 0 ? styles.even : styles.odd}>
                  <td style={styles.cell}>{call.id}</td>
                  <td style={styles.cell}>{call.state}</td>
                  <td style={styles.cell}>{call.operationName}</td>
                  <td style={styles.cell}>{call.callId}</td>
                  <td style={styles.cell}>{moment(call.lastChange).format('MMMM Do YYYY, hh:mm:ss')}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Panel>
        <Panel style={styles.panel} title={'Requests/responses to external systems'} >
          <table style={styles.table}>
            <tr>
              <th rowSpan={2} style={styles.header} >{'ID of executing'}</th>
              <th rowSpan={2} style={styles.header} >{'URI'}</th>
              <th style={styles.header} >{'Timestamp of request'}</th>
              <th style={styles.header} >{'Content of request'}</th>
              <th rowSpan={2} style={styles.header} >{'State'}</th>
            </tr>
            <tr>
              <th style={styles.header} >{'Timestamp of response'}</th>
              <th style={styles.header} >{'Content of response'}</th>
            </tr>
            {message.requests.map((request, index) => (
              <tbody key={request.id} style={index % 2 === 0 ? styles.even : styles.odd}>
                <tr>
                  <td rowSpan={2} style={styles.cell}>{request.id}</td>
                  <td rowSpan={2} style={styles.cell}>{request.uri}</td>
                  <td style={styles.cell}>{request.timestamp}</td>
                  <td style={styles.cell}>
                    <SyntaxHighlighter style={codeStyle} >{request.payload}</SyntaxHighlighter>
                  </td>
                  <td rowSpan={2} style={styles.cell}>{'N/A'}</td>
                </tr>
                <tr>
                  <td style={styles.cell}>{request.response.timestamp}</td>
                  <td style={styles.cell}>
                    <SyntaxHighlighter style={codeStyle} >{request.response.payload}</SyntaxHighlighter>
                  </td>
                </tr>
              </tbody>
              ))}
          </table>
        </Panel>
      </div >
    )
  }
}

Message.propTypes = {
  getMessage: PropTypes.func,
  restart: PropTypes.func,
  cancel: PropTypes.func,
  message: PropTypes.object,
  params: PropTypes.object
}

export default Message
