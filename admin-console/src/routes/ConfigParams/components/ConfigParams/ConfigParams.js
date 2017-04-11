import React, { PropTypes, Component } from 'react'
import Radium from 'radium'
import styles from './configParams.styles'
import ParamRow from '../ParamRow/ParamRow'
import EditParamModal from '../EditParamModal/EditParamModal'

@Radium
class ConfigParams extends Component {

  componentDidMount () {
    const { actions: { getConfigParams } } = this.props
    getConfigParams()
  }

  render () {
    const {
      configParams,
      paramDetail,
      updating,
      updateError,
      actions: { openParam, closeParam, updateParam }
    } = this.props

    if (!configParams) return <div>Loading...</div>

    const computedStyles = [styles.main]
    const data = configParams.data
    const categories = data.reduce((acc, item) => {
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
          <div key={cat} style={styles.category}>
            <h4 style={styles.categoryTitle}>{cat}</h4>
            <table style={styles.table}>
              <tbody>
                <tr>
                  <th style={styles.header}>Code</th>
                  <th style={styles.header}>Current Value</th>
                  <th style={styles.header}>Default Value</th>
                  <th style={styles.header}>Data Type</th>
                  <th style={styles.header}>Mandatory</th>
                  <th style={styles.header}>Description</th>
                  <th style={styles.header}>Validation</th>
                  <th style={styles.header}>Action</th>
                </tr>
                { categories[cat].map((row, count) => (
                  <ParamRow count={count} openParam={openParam} key={row.id} data={row} />
                ))}
              </tbody>
            </table>
          </div>
        ))}
      </div>
    )
  }
}

ConfigParams.propTypes = {
  configParams: PropTypes.object,
  actions: PropTypes.object,
  paramDetail: PropTypes.object,
  updating: PropTypes.bool,
  updateError: PropTypes.bool
}

export default ConfigParams
