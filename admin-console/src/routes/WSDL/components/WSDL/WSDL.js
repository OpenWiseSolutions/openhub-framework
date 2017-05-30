import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import styles from './wsdl.styles'
import Panel from '../../../../common/components/Panel/Panel'

@Radium
class WSDLOverview extends Component {
  componentDidMount () {
    const { getWsdlOverview } = this.props
    getWsdlOverview()
  }

  render () {
    const { wsdlData } = this.props

    if (!wsdlData) return <div>Loading...</div>

    return (
      <Panel style={styles.panel} title={'Input web service overview'}>
        <div>
          { wsdlData && wsdlData.map(({ name, wsdl }) =>
            <div key={name} style={styles.item}>
              <h3 style={styles.title}>{name}</h3>
              <div style={styles.content}>
                <a href={wsdl} target='_blank'>{wsdl}</a>
              </div>
            </div>
          )}
        </div>
      </Panel>
    )
  }
}

WSDLOverview.propTypes = {
  getWsdlOverview: PropTypes.func,
  wsdlData       : PropTypes.array
}

export default WSDLOverview
