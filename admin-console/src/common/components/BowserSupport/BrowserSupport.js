/* eslint-disable react/prop-types */

import React, { Component } from 'react'
import Card from 'react-md/lib/Cards/Card'
import styles from './browserSupport.styles'

const bowser = require('bowser')

class BrowserSupport extends Component {
  render () {
    // docs: https://github.com/lancedikson/bowser
    if (!bowser.isUnsupportedBrowser({ msie: '11', edge: '40' })) {
      return (
        <div style={styles.wrapper} >
          <Card style={styles.card} className='md-block-centered' >
            TODO: Text co ma user robit dalej...
          </Card >
        </div >
      )
    } else {
      return this.props.children
    }
  }
}

export default BrowserSupport
