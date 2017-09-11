/* eslint-disable react/prop-types */

import React, { Component } from 'react'
import { connect } from 'react-redux'
import { actions } from '../modules/home.module'
import Home from '../components/Home'

class HomeContainer extends Component {
  componentDidMount () {
    const {
      userData,
      getHealthInfo,
      getMetricsInfo,
      getOpenHubInfo
    } = this.props

    getOpenHubInfo()

    if (!userData) return

    getHealthInfo()
    getMetricsInfo()
  }

  render () {
    return <Home {...this.props} />
  }
}

const mapDispatchToProps = {
  ...actions
}

const mapStateToProps = ({ home, auth }) => ({
  dashboard: home,
  userData: auth.userData
})

export default connect(mapStateToProps, mapDispatchToProps)(HomeContainer)
