import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import LinearProgress from 'react-md/lib/Progress/LinearProgress'
import Card from 'react-md/lib/Cards/Card'
import CardTitle from 'react-md/lib/Cards/CardTitle'
import DataTable from 'react-md/lib/DataTables/DataTable'
import TableHeader from 'react-md/lib/DataTables/TableHeader'
import TableBody from 'react-md/lib/DataTables/TableBody'
import TableRow from 'react-md/lib/DataTables/TableRow'
import TableColumn from 'react-md/lib/DataTables/TableColumn'
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

    if (!configParams) return <LinearProgress id='loader' />

    const categories = configParams.reduce((acc, item) => {
      if (!acc[item.categoryCode]) acc[item.categoryCode] = []
      acc[item.categoryCode] = [ ...acc[item.categoryCode], item ]
      return acc
    }, {})

    return (
      <div style={styles.main} >
        { !!paramDetail && <EditParamModal
          updateError={updateError}
          updating={updating}
          close={closeParam}
          updateParam={updateParam}
          data={paramDetail}
          isOpen={!!paramDetail} /> }
        { Object.keys(categories).map((cat) => (
          <Card style={styles.card} key={cat}>
            <CardTitle title={cat} />
            <DataTable plain>
              <TableHeader >
                <TableRow >
                  <TableColumn >Code</TableColumn >
                  <TableColumn >Current Value</TableColumn >
                  <TableColumn >Default Value</TableColumn >
                  <TableColumn >Description</TableColumn >
                  <TableColumn >Action</TableColumn >
                </TableRow >
              </TableHeader >
              <TableBody >
                { categories[cat].map((row, count) => (
                  <ParamRow count={count} openParam={openParam} key={row.id} data={row} />
                ))}
              </TableBody >
            </DataTable >
          </Card>
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
