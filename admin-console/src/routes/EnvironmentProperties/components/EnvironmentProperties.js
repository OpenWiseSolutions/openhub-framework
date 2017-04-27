import React, { PropTypes, Component } from 'react'
import Radium from 'radium'
import styles from './environmentProperties.styles.js'
import ReactJson from 'react-json-view'
import Panel from '../../../common/components/Panel/Panel'

@Radium
class EnvironmentProperties extends Component {
  componentDidMount () {
    const { getEnvironmentData, getConfigData } = this.props
    getConfigData()
    getEnvironmentData()
  }

  render () {
    const {
      environmentData, configData
    } = this.props

    return (
      <div style={styles.main}>
        <Panel style={styles.panel} title={'Environment properties'}>
          <div style={styles.json}>
            {environmentData && <ReactJson src={environmentData} />}
          </div>
        </Panel>
        <Panel style={styles.panel} title={'Config properties'}>
          <div style={styles.json}>
            {configData && <ReactJson src={configData} />}
          </div>
        </Panel>
      </div>
    )
  }
}

EnvironmentProperties.propTypes = {
  getEnvironmentData: PropTypes.func,
  getConfigData: PropTypes.func,
  environmentData: PropTypes.object,
  configData: PropTypes.object
}

export default EnvironmentProperties
