import React, { PropTypes, Component } from 'react'
import Radium from 'radium'
import styles from './errorsOverview.styles'
import Panel from '../../../../common/components/Panel/Panel'

@Radium
class ErrorsOverview extends Component {
  componentDidMount () {
    const { getErrorsOverview } = this.props
    getErrorsOverview()
  }

  render () {
    const { errorsData } = this.props
    return (
      <div style={styles.main}>
        { errorsData && errorsData.map(({ name, codes }) =>
          <Panel key={name} style={styles.panel} title={name} >
            <table style={styles.table}>
              <tbody>
                <tr>
                  <th style={styles.header}>Code</th>
                  <th style={styles.header}>Description</th>
                  <th style={styles.header}>Recommended action</th>
                </tr>
                { codes.map(({ code, desc, action }, index) => (
                  <tr style={index % 2 === 0 ? styles.even : styles.odd}>
                    <td style={styles.cell}>{code}</td>
                    <td style={styles.cell}>{desc}</td>
                    <td style={styles.cell}>{action}</td>
                  </tr>
                ))
                }
              </tbody>
            </table>
          </Panel>
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
