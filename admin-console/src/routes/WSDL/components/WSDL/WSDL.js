import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import Card from 'react-md/lib/Cards/Card'
import CardTitle from 'react-md/lib/Cards/CardTitle'
import CardText from 'react-md/lib/Cards/CardText'
import styles from './wsdl.styles'

@Radium
class WSDLOverview extends Component {
  componentDidMount () {
    const { getWsdlOverview } = this.props
    getWsdlOverview()
  }

  render () {
    const { wsdlData } = this.props

    if (!wsdlData) return null

    return (
      <Card>
        <CardTitle title={'Input web service overview'} />
        <div className='md-grid'>
          { wsdlData && wsdlData.map(({ name, wsdl }) =>
            <Card className='md-cell md-cell--12' style={styles.item} key={name}>
              <CardTitle title={name} />
              <CardText>
                <a href={wsdl} target='_blank'>{wsdl}</a>
              </CardText>
            </Card>
          )}
        </div>
      </Card>
    )
  }
}

WSDLOverview.propTypes = {
  getWsdlOverview: PropTypes.func,
  wsdlData       : PropTypes.array
}

export default WSDLOverview
