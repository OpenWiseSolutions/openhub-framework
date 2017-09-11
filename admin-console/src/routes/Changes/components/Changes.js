import React, { PropTypes, Component } from 'react'
import Radium from 'radium'
import ReactMarkDown from 'react-markdown'
import Card from 'react-md/lib/Cards/Card'
import CardText from 'react-md/lib/Cards/CardText'
import styles from './changes.styles.js'

@Radium
class Changes extends Component {

  componentDidMount () {
    const { getChanges } = this.props
    getChanges()
  }

  render () {
    const { changesData } = this.props
    if (!changesData) return null
    return (
      <Card>
        <CardText>
          <div style={styles.markdown}>
            {changesData && <ReactMarkDown source={changesData} />}
          </div>
        </CardText >
      </Card >
    )
  }
}

Changes.propTypes = {
  getChanges: PropTypes.func,
  changesData: PropTypes.string
}

export default Changes
