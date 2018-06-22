/* eslint-disable react/prop-types */

import React, { Component } from 'react'
import Card from 'react-md/lib/Cards/Card'
import styles from './browserSupport.styles'

const bowser = require('bowser')

class BrowserSupport extends Component {
  render () {
    // docs: https://github.com/lancedikson/bowser
    if (bowser.msie) {
      return (
        <div style={styles.wrapper} >
          <Card style={styles.card} className='md-block-centered' >
            This browser is not supported, please upgrade to latest version of Chrome, Firefox or Edge.
          </Card >
        </div >
      )
    } else {
      return this.props.children
    }
  }
}

export default BrowserSupport
