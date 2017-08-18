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

@Radium
class Messages extends Component {
  componentDidMount () {
    this.props.getCatalog('messageState')
  }

  openDetail ({ id }) {
    hashHistory.push(`/messages/${id}`)
  }

  submit (event) {
    event.preventDefault()
    this.props.getMessages(this.state)
  }

  render () {
    const { messages, updateFilter, resetFilter, filter, messageStates, noMessages } = this.props
    if (!filter || !messageStates) return null

    const messageStatesOptions = messageStates
      .map(({ code, description }) => ({
        label: description,
        value: code
      }))

    return (
      <Card >
        <CardTitle subtitle={'Filter'} />
        <form onSubmit={this.submit.bind(this)} >
          <div className='md-grid' >
            <div className='md-cell md-cell--12' >
              <TextField
                id='fulltext'
                value={filter.fulltext}
                onChange={(fulltext) => updateFilter('fulltext', fulltext)}
                label='Fulltext'
              />
            </div >
            <div className='md-cell md-cell--6' >
              <label style={{ color: 'gray' }} >Received From</label >
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
                label='Source System'
              />
              <TextField
                id='processId'
                value={filter.processId}
                onChange={(processId) => updateFilter('processId', processId)}
                label='Process ID'
              />
              <TextField
                id='errorCode'
                value={filter.errorCode}
                onChange={(errorCode) => updateFilter('errorCode', errorCode)}
                label='Error Code'
              />
              <TextField
                id='operationName'
                value={filter.operationName}
                onChange={(operationName) => updateFilter('operationName', operationName)}
                label='Operation Name'
              />
            </div >
            <div className='md-cell md-cell--6' >
              <label style={{ color: 'gray' }} >Received To</label >
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
                label='Correlation ID'
              />
              <SelectField
                id='state'
                fullWidth
                value={filter.state}
                onChange={(state) => updateFilter('state', state)}
                menuItems={messageStatesOptions}
                label='State'
              />
              <TextField
                id='serviceName'
                value={filter.serviceName}
                onChange={(serviceName) => updateFilter('serviceName', serviceName)}
                label='Service Name'
              />
              <div style={{ marginTop: '25px' }} >
                <Button
                  className='md-cell md-cell--6'
                  primary raised
                  type='submit'
                  label={'Search'}
                />
                <Button
                  className='md-cell md-cell--6'
                  raised
                  onClick={() => resetFilter()}
                  label={'Reset'} type='button'
                />
              </div >
            </div >
          </div >
        </form >
        { noMessages && <div className='md-cell md-cell--12'>
          <CardTitle subtitle={'No Messages'} />
        </div>}
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
  messageStates: PropTypes.array,
  getMessages: PropTypes.func,
  updateFilter: PropTypes.func,
  resetFilter: PropTypes.func,
  getCatalog: PropTypes.func,
  noMessages: PropTypes.bool
}

export default Messages
