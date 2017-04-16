import React, { PropTypes, Component } from 'react'
import Radium from 'radium'
import { reverse, length, take } from 'ramda'
import { Field, ValidStyles } from 'valid-react-form'
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

  componentDidMount () {
    const { getLoggers } = this.props
    getLoggers()
  }

  updateSearchQuery (value) {
    const { loggingData } = this.props
    if (!loggingData) return
    let filtered = loggingData.loggers.filter((item) => item.name.includes(value))
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
    if (!loggingData) return <div>Loading...</div>
    const levels = reverse(loggingData.levels)
    const items = take(LIST_LENGTH * takes, filtered || loggingData.loggers)
    const count = `${length(filtered || loggingData.loggers)} / ${length(loggingData.loggers)}`

    return (
      <div style={styles.main}>
        <div style={styles.searchBox}>
          <ValidStyles>
            <Field onChange={(val) => this.updateSearchQuery(val)}
              value={value}
              placeholder='filter'
              name='searchQuery' />
          </ValidStyles>
          <div style={styles.counts}>{count}</div>
        </div>
        <div style={styles.loggers}>
          {items.map(logger => <LoggerRow
            key={logger.name}
            updateLogger={updateLogger}
            configuredLevel={logger.data.configuredLevel}
            levels={levels}
            label={logger.name}
          />)}
        </div>
        {items.length >= LIST_LENGTH && items.length < loggingData.loggers.length &&
        <div>
          <div key='showMore' onClick={() => this.showMore()} style={styles.listControl}>show more</div>
          <div key='showAll' onClick={() => this.showAll()} style={styles.listControl}>show all</div>
        </div>}
      </div>
    )
  }
}

ConfigLogging.propTypes = {
  getLoggers: PropTypes.func,
  updateLogger: PropTypes.func,
  loggingData: PropTypes.object
}

export default ConfigLogging
