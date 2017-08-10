import React, { PropTypes, Component } from 'react'
import Radium from 'radium'
import ReactJson from 'react-json-view'
import LinearProgress from 'react-md/lib/Progress/LinearProgress'
import Card from 'react-md/lib/Cards/Card'
import CardTitle from 'react-md/lib/Cards/CardTitle'
import styles from './environmentProperties.styles.js'

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

    if (!environmentData || !configData) {
      return <LinearProgress id='progress' />
    }

    return (
      <div style={styles.main}>
        <Card >
          <CardTitle title={'Environment properties'} />
          <div style={styles.json}>
            {environmentData && <ReactJson src={environmentData} />}
          </div>
        </Card>
        <br />
        <Card>
          <CardTitle title={'Config properties'} />
          <div style={styles.json}>
            {configData && <ReactJson src={configData} />}
          </div>
        </Card>
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
