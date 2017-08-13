/* eslint-disable react/jsx-no-bind */
import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import moment from 'moment'
import Flatpickr from 'react-flatpickr'
import DataTable from 'react-md/lib/DataTables/DataTable'
import TableHeader from 'react-md/lib/DataTables/TableHeader'
import TableBody from 'react-md/lib/DataTables/TableBody'
import TableRow from 'react-md/lib/DataTables/TableRow'
import TableColumn from 'react-md/lib/DataTables/TableColumn'
import SelectField from 'react-md/lib/SelectFields'
import Button from 'react-md/lib/Buttons/Button'
import TextField from 'react-md/lib/TextFields'
import Card from 'react-md/lib/Cards/Card'
import CardTitle from 'react-md/lib/Cards/CardTitle'
import { hashHistory } from 'react-router'
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
      receivedFrom: moment().subtract(5, 'minute').format()
    }
  }

  componentDidMount () {
    this.props.getMessages(this.state)
  }

  openDetail ({ id }) {
    hashHistory.push(`/messages/${id}`)
  }

  submit (event) {
    event.preventDefault()
    this.props.getMessages(this.state)
  }

  reset () {
    this.setState(() => ({
      receivedFrom: moment().subtract(5, 'minute').format(),
      lastChangeFrom: '',
      sourceSystem: '',
      processId: '',
      errorCode: '',
      operationName: '',
      receivedTo: '',
      lastChangeTo: '',
      correlationId: '',
      state: '',
      serviceName: '',
      fulltext: ''
    }))
  }

  render () {
    const { messages } = this.props
    return (
      <Card>
        <CardTitle subtitle={'Filter'} />
        <form onSubmit={this.submit.bind(this)} >
          <div className='md-grid' >
            <div className='md-cell md-cell--6' >
              <Flatpickr
                style={styles.datepicker}
                value={this.state.receivedFrom}
                onChange={(receivedFrom) => this.setState(() => ({ receivedFrom }))}
                placeholder='Received From'
                data-enable-time
              />
              <Flatpickr
                style={styles.datepicker}
                value={this.state.lastChangeFrom}
                onChange={(lastChangeFrom) => this.setState(() => ({ lastChangeFrom }))}
                placeholder='Last Change From'
                data-enable-time
              />
              <TextField
                id='sourceSystem'
                value={this.state.sourceSystem}
                onChange={(sourceSystem) => this.setState(() => ({ sourceSystem }))}
                placeholder='Source System'
              />
              <TextField
                id='processId'
                value={this.state.processId}
                onChange={(processId) => this.setState(() => ({ processId }))}
                placeholder='Process ID'
              />
              <TextField
                id='errorCode'
                value={this.state.errorCode}
                onChange={(errorCode) => this.setState(() => ({ errorCode }))}
                placeholder='Error Code'
              />
              <TextField
                id='operationName'
                value={this.state.operationName}
                onChange={(operationName) => this.setState(() => ({ operationName }))}
                placeholder='Operation Name'
              />
            </div >
            <div className='md-cell md-cell--6' >
              <Flatpickr
                style={styles.datepicker}
                value={this.state.receivedTo}
                onChange={(receivedTo) => this.setState(() => ({ receivedTo }))}
                placeholder='Received To'
                data-enable-time
              />
              <Flatpickr
                style={styles.datepicker}
                value={this.state.lastChangeTo}
                onChange={(lastChangeTo) => this.setState(() => ({ lastChangeTo }))}
                placeholder='Last Change To'
                data-enable-time
              />
              <TextField
                id='correlationId'
                value={this.state.correlationId}
                onChange={(correlationId) => this.setState(() => ({ correlationId }))}
                placeholder='Correlation ID'
              />
              <SelectField
                id='state'
                fullWidth
                value={this.state.state}
                onChange={(state) => this.setState(() => ({ state }))}
                menuItems={states}
                placeholder='State'
              />
              <TextField
                id='serviceName'
                value={this.state.serviceName}
                onChange={(serviceName) => this.setState(() => ({ serviceName }))}
                placeholder='Service Name'
              />
              <TextField
                id='fulltext'
                value={this.state.fulltext}
                onChange={(fulltext) => this.setState(() => ({ fulltext }))}
                placeholder='Fulltext'
              />
            </div >
          </div >
          <div className='md-text-right' >
            <Button
              className='md-cell md-cell--3'
              primary raised
              type='submit'
              label={'Submit'}
            />
            <Button
              className='md-cell md-cell--3'
              raised
              onClick={() => this.reset()}
              label={'Reset'} type='button'
            />
          </div >
        </form >

        <CardTitle subtitle={messages.length > 0 ? 'Messages' : 'No Messages'} />
        {messages && messages.length > 0 &&
        <DataTable plain >
          <TableHeader >
            <TableRow >
              <TableColumn >Correlation ID</TableColumn >
              <TableColumn >Source System</TableColumn >
              <TableColumn >Received Time</TableColumn >
              <TableColumn >Start Process Time</TableColumn >
              <TableColumn >State</TableColumn >
              <TableColumn >Error Code</TableColumn >
              <TableColumn >Service</TableColumn >
              <TableColumn >Operation</TableColumn >
            </TableRow >
          </TableHeader >
          <TableBody >
            {messages.map((message, index) => (
              <TableRow style={styles.row} onClick={() => this.openDetail(message)} key={message.id} >
                <TableColumn >{message.correlationId}</TableColumn >
                <TableColumn >{message.sourceSystem}</TableColumn >
                <TableColumn >{moment(message.received).format('YYYY-MM-DD hh:mm:ss')}</TableColumn >
                <TableColumn >{moment(message.processingStarted).format('YYYY-MM-DD hh:mm:ss')}</TableColumn >
                <TableColumn >{message.state}</TableColumn >
                <TableColumn >{message.errorCode}</TableColumn >
                <TableColumn >{message.serviceName}</TableColumn >
                <TableColumn >{message.operationName}</TableColumn >
              </TableRow >
            ))}
          </TableBody >
        </DataTable >}
      </Card >
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
