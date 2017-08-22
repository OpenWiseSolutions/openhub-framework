import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import tc from 'tinycolor2'
import { PieChart, Pie } from 'recharts'
import Card from 'react-md/lib/Cards/Card'
import CardTitle from 'react-md/lib/Cards/CardTitle'
import Divider from 'react-md/lib/Dividers'
import List from 'react-md/lib/Lists/List'
import ListItem from 'react-md/lib/Lists/ListItem'
import ms2hrs from '../../../utils/ms2hrs'
import styles from './home.styles'
import { positiveColor, secondaryColor } from '../../../styles/colors'
import LoginCard from '../../../common/containers/loginCard.container'
import Status from '../../../common/components/Status/Status'

@Radium
class Home extends Component {
  render () {
    const { userData, dashboard: { healthInfo, openHubInfo, metricsInfo } } = this.props

    if (!openHubInfo && !healthInfo && !metricsInfo) {
      return null
    }

    if (!userData) {
      return (
        <div style={styles.container} >
          <LoginCard
            name={openHubInfo.name}
            version={openHubInfo.version + ' (' + openHubInfo.core.version + ')'} />
        </div >
      )
    }

    const isUp = (val) => val === 'UP'

    const appTableData = openHubInfo && [
      ['Name', openHubInfo.name],
      ['Version', openHubInfo.version + ' (' + openHubInfo.core.version + ')']
    ]

    const healthTableData = healthInfo && [
      ['Application', <Status status={isUp(healthInfo.status)} />],
      ['Datasources', <Status status={isUp(healthInfo.db.status)} />],
      ['Database', healthInfo.db.database]
    ]

    const diskTableData = healthInfo && [
      ['Status', <Status status={isUp(healthInfo.diskSpace.status)} />],
      ['Free space', (healthInfo.diskSpace.free / 1000000000).toFixed(2) + ' GB'],
      ['Total space', (healthInfo.diskSpace.total / 1000000000).toFixed(2) + ' GB'],
      ['Threshold', (healthInfo.diskSpace.threshold / 1000000000).toFixed(2) + ' GB']
    ]

    const memChartProps = metricsInfo && {
      cx: 100,
      cy: 100,
      innerRadius: 0,
      outerRadius: 100,
      fill: secondaryColor,
      data: [
        { value: metricsInfo['mem.free'], fill: tc(positiveColor).setAlpha(0.8).toString() },
        { value: metricsInfo['mem'], fill: tc(secondaryColor).setAlpha(0.3).toString() }
      ]
    }

    const heapChartProps = metricsInfo && {
      cx: 100,
      cy: 100,
      innerRadius: 0,
      outerRadius: 100,
      fill: secondaryColor,
      data: [
        { value: metricsInfo['heap.committed'], fill: tc(positiveColor).setAlpha(0.8).toString() },
        { value: metricsInfo['heap'], fill: tc(secondaryColor).setAlpha(0.3).toString() }
      ]
    }

    const JVMTableData = metricsInfo && [
      ['Available CPUs', metricsInfo.processors],
      ['Uptime', ms2hrs(metricsInfo.uptime)],
      ['Current loaded classes', metricsInfo['classes.loaded']],
      ['Total classes', metricsInfo['classes']],
      ['Unloaded classes', metricsInfo['classes.unloaded']]
    ]

    const freeMemory = metricsInfo && `${(metricsInfo['mem.free'] / 1000).toFixed(2)} MB`
    const usedMemory = metricsInfo &&
      `${((metricsInfo['mem'] - metricsInfo['mem.free']) / 1000).toFixed(2)} MB`
    const totalMemory = metricsInfo && `${(metricsInfo['mem'] / 1000).toFixed(2)} MB`

    const freeHeap = metricsInfo && `${(metricsInfo['heap.committed'] / 1000).toFixed(2)} MB`
    const usedHeap = metricsInfo &&
      `${((metricsInfo['heap'] - metricsInfo['heap.committed']) / 1000).toFixed(2)} MB`
    const totalHeap = metricsInfo && `${(metricsInfo['heap'] / 1000).toFixed(2)} MB`

    return (
      <div >
        {userData &&
        <div style={styles.widgets} >
          {healthTableData && <Card style={styles.widget} >
            <CardTitle title={'Health'} />
            <Divider />
            <List >
              {healthTableData.map((i, k) => (
                <ListItem className='md-pointer--none' key={i[0]} primaryText={i[0]} rightIcon={i[1]} />
              ))}
            </List >
          </Card >}

          {diskTableData && <Card style={styles.widget} >
            <CardTitle title={'Disk Usage'} />
            <Divider />
            <List >
              {diskTableData.map((i, k) => (
                <ListItem className='md-pointer--none' key={i[0]} primaryText={i[0]} rightIcon={i[1]} />
              ))}
            </List >
          </Card >}

          {memChartProps &&
          <Card style={styles.widget} >
            <CardTitle title={'Memory Usage'} />
            <Divider />
            <div className='md-grid' >
              <div className='md-cell md-cell--6' >
                <PieChart width={200} height={200} >
                  <Pie isAnimationActive={false} {...memChartProps} />
                </PieChart >
              </div >
              <List className='md-cell md-cell--6' >
                <ListItem className='md-pointer--none' primaryText={'Free memory'} rightIcon={freeMemory} />
                <ListItem className='md-pointer--none' primaryText={'Used memory'} rightIcon={usedMemory} />
                <ListItem className='md-pointer--none' primaryText={'Total memory'} rightIcon={totalMemory} />
              </List >
            </div >
          </Card >}

          {heapChartProps &&
          <Card style={styles.widget} >
            <CardTitle title={'Memory Heap'} />
            <Divider />
            <div className='md-grid' >
              <div className='md-cell md-cell--6' >
                <PieChart width={200} height={200} >
                  <Pie isAnimationActive={false} {...heapChartProps} />
                </PieChart >
              </div >
              <List className='md-cell md-cell--6' >
                <ListItem className='md-pointer--none' primaryText={'Free heap'} rightIcon={freeHeap} />
                <ListItem className='md-pointer--none' primaryText={'Used heap'} rightIcon={usedHeap} />
                <ListItem className='md-pointer--none' primaryText={'Total heap'} rightIcon={totalHeap} />
              </List >
            </div >
          </Card >}

          {JVMTableData && <Card style={styles.widget} >
            <CardTitle title={'JVM Information'} />
            <Divider />
            <List >
              {JVMTableData.map((i, k) => (
                <ListItem className='md-pointer--none' key={i[0]} primaryText={i[0]} rightIcon={i[1]} />
              ))}
            </List >
          </Card >}

          {appTableData && <Card style={styles.widget} >
            <CardTitle title={'Application Information'} />
            <Divider />
            <List >
              {appTableData.map((i, k) => (
                <ListItem className='md-pointer--none' key={i[0]} primaryText={i[0]} rightIcon={i[1]} />
              ))}
            </List >
          </Card >}
        </div >}
      </div >
    )
  }
}

Home.propTypes = {
  userData: PropTypes.object,
  dashboard: PropTypes.object.isRequired,
  getHealthInfo: PropTypes.func,
  getMetricsInfo: PropTypes.func
}

export default Home
