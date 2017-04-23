import React, { PropTypes, Component } from 'react'
import Radium from 'radium'
import ReactMarkDown from 'react-markdown'
import Panel from '../../../common/components/Panel/Panel'
import styles from './changes.styles.js'

@Radium
class Changes extends Component {

  componentDidMount () {
    const { getChanges } = this.props
    getChanges()
  }

  render () {
    const { changesData } = this.props

    return (
      <Panel style={styles.main} title={'Changes'}>
        <div style={styles.markdown}>
          {changesData && <ReactMarkDown source={changesData} />}
        </div>
      </Panel>
    )
  }
}

Changes.propTypes = {
  getChanges: PropTypes.func,
  changesData: PropTypes.string
}

export default Changes
