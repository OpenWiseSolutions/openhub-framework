import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { hashHistory } from 'react-router'
import moment from 'moment'
import Radium from 'radium'
import SyntaxHighlighter, { registerLanguage } from 'react-syntax-highlighter/dist/light'
import xml from 'react-syntax-highlighter/dist/languages/xml'
import xcode from 'react-syntax-highlighter/dist/styles/xcode'
import Card from 'react-md/lib/Cards/Card'
import CardTitle from 'react-md/lib/Cards/CardTitle'
import Checkbox from 'react-md/lib/SelectionControls/Checkbox'
import DataTable from 'react-md/lib/DataTables/DataTable'
import TableHeader from 'react-md/lib/DataTables/TableHeader'
import TableBody from 'react-md/lib/DataTables/TableBody'
import TableRow from 'react-md/lib/DataTables/TableRow'
import TableColumn from 'react-md/lib/DataTables/TableColumn'
import Button from 'react-md/lib/Buttons/Button'
import styles from './message.styles'

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

    if (!message) return null

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
      { t: 'Content (body) of message', v: <SyntaxHighlighter style={codeStyle} >{message.body}</SyntaxHighlighter > },
      { t: 'Whole incoming message', v: <SyntaxHighlighter style={codeStyle} >{message.envelope}</SyntaxHighlighter > },
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
      <Card >
        <Card >
          <CardTitle
            subtitle={<Button
              onClick={() => hashHistory.push('/messages')}
              label='Go Back To Messages'
              icon >
              keyboard_arrow_left
            </Button >}
          />
          <DataTable plain >
            <TableHeader >
              <TableRow >
                <TableColumn >Attribute Name</TableColumn >
                <TableColumn adjusted >Attribute Value</TableColumn >
              </TableRow >
            </TableHeader >
            <TableBody >
              {rows.map((row, index) => (
                <TableRow key={row.t} >
                  <TableColumn >{row.t}</TableColumn >
                  <TableColumn adjusted >{row.v}</TableColumn >
                </TableRow >
              ))}
            </TableBody >
          </DataTable >
        </Card >
        <br />
        <Card className='md-divider-border md-divider-border--top' >
          <div style={styles.controls} >
            <Checkbox
              id='total'
              name='total'
              label='total'
              disabled={!states.includes(message.state)}
              value={this.state.totalCheckbox}
              onChange={() => this.toggle()}
            />
            <Button
              raised
              disabled={!states.includes(message.state)}
              type='button'
              primary
              onClick={() => restart(message.id, this.state.totalCheckbox)}
              label={'Restart'}
            />
            <Button
              raised
              disabled={states.includes(message.state)}
              type='button'
              onClick={() => cancel(message.id)}
              label={'Cancel'}
            />
          </div >
        </Card >
        <br />
        <Card >
          <CardTitle subtitle={'List of external calls'} />

          <DataTable plain >
            <TableHeader >
              <TableRow >
                <TableColumn >Internal ID</TableColumn >
                <TableColumn >State</TableColumn >
                <TableColumn >Operation Name</TableColumn >
                <TableColumn >ID of Call</TableColumn >
                <TableColumn >Last Change Time</TableColumn >
              </TableRow >
            </TableHeader >
            <TableBody >
              {message.externalCalls.map((call, index) => (
                <TableRow key={call.id} >
                  <TableColumn >{call.id}</TableColumn >
                  <TableColumn >{call.state}</TableColumn >
                  <TableColumn >{call.operationName}</TableColumn >
                  <TableColumn >{call.callId}</TableColumn >
                  <TableColumn >{moment(call.lastChange).format('MMMM Do YYYY, hh:mm:ss')}</TableColumn >
                </TableRow >
              ))}
            </TableBody >
          </DataTable >

        </Card >
        <br />
        <Card >
          <CardTitle subtitle={'Requests/responses to external systems'} />

          <DataTable plain >
            <TableHeader >
              <TableRow >
                <TableColumn >ID of Executing</TableColumn >
                <TableColumn >URI</TableColumn >
                <TableColumn >Timestamp of Request</TableColumn >
                <TableColumn >Timestamp of Response</TableColumn >
                <TableColumn >Content of Request</TableColumn >
                <TableColumn >Content of Response</TableColumn >
                <TableColumn >State</TableColumn >
              </TableRow >
            </TableHeader >
            <TableBody >
              {message.requests.map((request, index) => (
                <TableRow key={request.id} >
                  <TableColumn >{request.id}</TableColumn >
                  <TableColumn >{request.uri}</TableColumn >
                  <TableColumn >{request.timestamp}</TableColumn >
                  <TableColumn >{request.response.timestamp}</TableColumn >
                  <TableColumn >
                    {<SyntaxHighlighter style={codeStyle} >{request.payload}</SyntaxHighlighter >}
                  </TableColumn >
                  <TableColumn >
                    {<SyntaxHighlighter style={codeStyle} >{request.response.payload}</SyntaxHighlighter >}
                  </TableColumn >
                  <TableColumn >{request.state}</TableColumn >
                </TableRow >
              ))}
            </TableBody >
          </DataTable >
        </Card >
      </Card >
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
