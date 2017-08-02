import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import Panel from '../../../../common/components/Panel/Panel'
import styles from './configParams.styles'
import ParamRow from '../ParamRow/ParamRow'
import EditParamModal from '../EditParamModal/EditParamModal'

@Radium
class ConfigParams extends Component {

  componentDidMount () {
    const { getConfigParams } = this.props
    getConfigParams()
  }

  render () {
    const {
      configParams,
      paramDetail,
      updating,
      updateError,
      openParam,
      closeParam,
      updateParam
    } = this.props

    if (!configParams) return <div>Loading...</div>

    const computedStyles = [styles.main]
    const categories = configParams.reduce((acc, item) => {
      if (!acc[item.categoryCode]) acc[item.categoryCode] = []
      acc[item.categoryCode] = [ ...acc[item.categoryCode], item ]
      return acc
    }, {})

    return (
      <div style={computedStyles} >
        <EditParamModal
          updateError={updateError}
          updating={updating}
          close={closeParam}
          updateParam={updateParam}
          data={paramDetail}
          isOpen={!!paramDetail} />
        { Object.keys(categories).map((cat) => (
          <Panel title={cat} key={cat} style={styles.panel}>
            <table style={styles.table}>
              <tbody>
                <tr>
                  <th style={styles.header}>Code</th>
                  <th style={styles.header}>Current Value</th>
                  <th style={styles.header}>Default Value</th>
                  <th style={styles.header}>Description</th>
                  <th style={styles.header}>Action</th>
                </tr>
                { categories[cat].map((row, count) => (
                  <ParamRow count={count} openParam={openParam} key={row.id} data={row} />
                ))}
              </tbody>
            </table>
          </Panel>
        ))}
      </div>
    )
  }
}

ConfigParams.propTypes = {
  configParams: PropTypes.array,
  openParam: PropTypes.func,
  closeParam: PropTypes.func,
  updateParam: PropTypes.func,
  getConfigParams: PropTypes.func,
  paramDetail: PropTypes.object,
  updating: PropTypes.bool,
  updateError: PropTypes.bool
}

export default ConfigParams
