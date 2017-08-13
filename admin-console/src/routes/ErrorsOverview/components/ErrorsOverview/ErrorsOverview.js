import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import styles from './errorsOverview.styles'
import Card from 'react-md/lib/Cards/Card'
import CardTitle from 'react-md/lib/Cards/CardTitle'
import DataTable from 'react-md/lib/DataTables/DataTable'
import TableHeader from 'react-md/lib/DataTables/TableHeader'
import TableBody from 'react-md/lib/DataTables/TableBody'
import TableRow from 'react-md/lib/DataTables/TableRow'
import TableColumn from 'react-md/lib/DataTables/TableColumn'

@Radium
class ErrorsOverview extends Component {
  componentDidMount () {
    const { getErrorsOverview } = this.props
    getErrorsOverview()
  }

  render () {
    const { errorsData } = this.props

    if (!errorsData) return null

    return (
      <div style={styles.main}>
        { errorsData && errorsData.map(({ name, codes }) =>
          <Card style={styles.card} key={name} >
            <CardTitle title={name} />
            <DataTable plain >
              <TableHeader >
                <TableRow >
                  <TableColumn >Code</TableColumn >
                  <TableColumn >Description</TableColumn >
                  <TableColumn >Recommended action</TableColumn >
                </TableRow >
              </TableHeader >
              <TableBody >
                {codes.map(({ code, desc, action }, index) => (
                  <TableRow key={code} >
                    <TableColumn>{code}</TableColumn>
                    <TableColumn>{desc}</TableColumn>
                    <TableColumn>{action}</TableColumn>
                  </TableRow>
                ))}
              </TableBody>
            </DataTable>
          </Card>
        )}
      </div>
    )
  }
}

ErrorsOverview.propTypes = {
  getErrorsOverview: PropTypes.func,
  errorsData: PropTypes.array
}

export default ErrorsOverview
