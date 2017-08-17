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

  render () {
    const { messages, updateFilter, resetFilter, filter } = this.props
    if (!filter) return null
    return (
      <Card >
        <CardTitle subtitle={'Filter'} />
        <form onSubmit={this.submit.bind(this)} >
          <div className='md-grid' >
            <div className='md-cell md-cell--6' >
              <label >Received From</label >
              <Flatpickr
                style={styles.datepicker}
                value={filter.receivedFrom}
                onChange={(receivedFrom) => updateFilter('receivedFrom', receivedFrom)}
                placeholder='Received From'
                data-enable-time
              />
              <Flatpickr
                style={styles.datepicker}
                value={filter.lastChangeFrom}
                onChange={(lastChangeFrom) => updateFilter('lastChangeFrom', lastChangeFrom)}
                placeholder='Last Change From'
                data-enable-time
              />
              <TextField
                id='sourceSystem'
                value={filter.sourceSystem}
                onChange={(sourceSystem) => updateFilter('sourceSystem', sourceSystem)}
                placeholder='Source System'
              />
              <TextField
                id='processId'
                value={filter.processId}
                onChange={(processId) => updateFilter('processId', processId)}
                placeholder='Process ID'
              />
              <TextField
                id='errorCode'
                value={filter.errorCode}
                onChange={(errorCode) => updateFilter('errorCode', errorCode)}
                placeholder='Error Code'
              />
              <TextField
                id='operationName'
                value={filter.operationName}
                onChange={(operationName) => updateFilter('operationName', operationName)}
                placeholder='Operation Name'
              />
            </div >
            <div className='md-cell md-cell--6' >
              <label >Received To</label >
              <Flatpickr
                style={styles.datepicker}
                value={filter.receivedTo}
                onChange={(receivedTo) => updateFilter('receivedTo', receivedTo)}
                data-enable-time
              />
              <Flatpickr
                style={styles.datepicker}
                value={filter.lastChangeTo}
                onChange={(lastChangeTo) => updateFilter('lastChangeTo', lastChangeTo)}
                placeholder='Last Change To'
                data-enable-time
              />
              <TextField
                id='correlationId'
                value={filter.correlationId}
                onChange={(correlationId) => updateFilter('correlationId', correlationId)}
                placeholder='Correlation ID'
              />
              <SelectField
                id='state'
                fullWidth
                value={filter.state}
                onChange={(state) => updateFilter('state', state)}
                menuItems={states}
                placeholder='State'
              />
              <TextField
                id='serviceName'
                value={filter.serviceName}
                onChange={(serviceName) => updateFilter('serviceName', serviceName)}
                placeholder='Service Name'
              />
              <TextField
                id='fulltext'
                value={filter.fulltext}
                onChange={(fulltext) => updateFilter('fulltext', fulltext)}
                placeholder='Fulltext'
              />
            </div >
          </div >
          <div className='md-text-right' >
            <Button
              className='md-cell md-cell--3'
              primary raised
              type='submit'
              label={'Search'}
            />
            <Button
              className='md-cell md-cell--3'
              raised
              onClick={() => resetFilter()}
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

Messages.propTypes = {
  messages: PropTypes.array,
  filter: PropTypes.object,
  getMessages: PropTypes.func,
  updateFilter: PropTypes.func,
  resetFilter: PropTypes.func
}

export default Messages
