/* eslint-disable max-len */

import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import { reverse, length, take } from 'ramda'
import Card from 'react-md/lib/Cards/Card'
import TextField from 'react-md/lib/TextFields'
import FontIcon from 'react-md/lib/FontIcons'
import DataTable from 'react-md/lib/DataTables/DataTable'
import TableHeader from 'react-md/lib/DataTables/TableHeader'
import TableBody from 'react-md/lib/DataTables/TableBody'
import TableRow from 'react-md/lib/DataTables/TableRow'
import TableColumn from 'react-md/lib/DataTables/TableColumn'
import Button from 'react-md/lib/Buttons/Button'
import styles from './configLogging.styles.js'
import LoggerRow from '../LoggerRow/LoggerRow'

const LIST_LENGTH = 10

@Radium
class ConfigLogging extends Component {

  constructor (props) {
    super(props)
    this.state = {
      value: '',
      filtered: null,
      takes: 1
    }
  }

  componentDidUpdate (prevProps) {
    const { value } = this.state
    const { loggingData } = this.props
    if (prevProps.loggingData !== loggingData) {
      this.updateSearchQuery(value)
    }
  }

  componentDidMount () {
    const { getLoggers } = this.props
    getLoggers()
  }

  updateSearchQuery (value) {
    const { loggingData } = this.props
    if (!loggingData) return
    let filtered = loggingData.loggers.filter((item) => item.name.toLowerCase().includes(value.toLowerCase()))
    this.setState(() => ({
      value,
      filtered
    }))
  }

  showMore () {
    this.setState(({ takes }) => ({
      takes: ++takes
    }))
  }

  showAll () {
    this.setState(({ takes }) => ({
      takes: 1000
    }))
  }

  render () {
    const { filtered, value, takes } = this.state
    const { loggingData, updateLogger } = this.props

    if (!loggingData) return null

    const levels = reverse(loggingData.levels)
    const items = take(LIST_LENGTH * takes, filtered || loggingData.loggers)
    const count = `${length(filtered || loggingData.loggers)} / ${length(loggingData.loggers)}`

    return (
      <Card >
        <div style={styles.main} >
          <div style={styles.searchBox} >
            <TextField
              onChange={(val) => this.updateSearchQuery(val)}
              value={value}
              leftIcon={<FontIcon >search</FontIcon >}
              rightIcon={<div >{count}</div >}
              placeholder='Filter'
              name='searchQuery'
            />
            <Button
              style={{ marginTop: '3px', marginLeft: '20px', opacity: this.state.value ? 1 : 0 }}
              onClick={() => this.updateSearchQuery('')}
              icon
            >close
            </Button >
          </div >
          <br />
          <br />
          <DataTable plain >
            <TableHeader >
              <TableRow >
                <TableColumn >Name</TableColumn >
                <TableColumn >Actions</TableColumn >
              </TableRow >
            </TableHeader >
            <TableBody >
              {items.map(logger => <LoggerRow
                key={logger.name}
                updateLogger={updateLogger}
                configuredLevel={logger.data.configuredLevel}
                levels={levels}
                label={logger.name}
              />)}
            </TableBody >
          </DataTable >
          {items.length >= LIST_LENGTH && items.length < loggingData.loggers.length &&
          <div style={styles.controls} >
            <Button onClick={() => this.showMore()} flat label={`show more (+${((takes + 1) * 10) > length(loggingData.loggers) ? length(loggingData.loggers) - (takes * 10) : 10})`} />
            <Button onClick={() => this.showAll()} flat label={`show all (${length(loggingData.loggers)})`} />
          </div >}
        </div >
      </Card >
    )
  }
}

ConfigLogging.propTypes = {
  getLoggers: PropTypes.func,
  updateLogger: PropTypes.func,
  loggingData: PropTypes.object
}

export default ConfigLogging
