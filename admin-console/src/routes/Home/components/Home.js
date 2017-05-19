import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import { PieChart, Pie } from 'recharts'
import ms2hrs from '../../../utils/ms2hrs'
import styles from './home.styles'
import { positiveColor, secondaryColor } from '../../../styles/colors'
import Panel from '../../../common/components/Panel/Panel'
import Table from '../../../common/components/Table/Table'
import Status from '../../../common/components/Status/Status'

@Radium
class Home extends Component {

  componentDidMount () {
    const { getHealthInfo, getMetricsInfo } = this.props
    getHealthInfo()
    getMetricsInfo()
  }

  render () {
    const { userData, dashboard: { healthInfo, openHubInfo, metricsInfo } } = this.props
    if (!openHubInfo && !healthInfo && !metricsInfo) return <div>Loading...</div>

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
        { value: metricsInfo['mem.free'], fill: positiveColor },
        { value: metricsInfo['mem'], fill: secondaryColor }
      ]
    }

    const heapChartProps = metricsInfo && {
      cx: 100,
      cy: 100,
      innerRadius: 0,
      outerRadius: 100,
      fill: secondaryColor,
      data: [
        { value: metricsInfo['heap.committed'], fill: positiveColor },
        { value: metricsInfo['heap'], fill: secondaryColor }
      ]
    }

    const JVMTableData = metricsInfo && [
        ['Available CPUs', metricsInfo.processors],
        ['Uptime', ms2hrs(metricsInfo.uptime)],
        ['Current loaded classes', metricsInfo['classes.loaded']],
        ['Total classes', metricsInfo['classes']],
        ['Unloaded classes', metricsInfo['classes.unloaded']]
    ]

    const freeMemory = metricsInfo && `Free memory: ${(metricsInfo['mem.free'] / 1000).toFixed(2)} MB`
    const usedMemory = metricsInfo &&
      `Used memory: ${((metricsInfo['mem'] - metricsInfo['mem.free']) / 1000).toFixed(2)} MB`
    const totalMemory = metricsInfo && `Total memory: ${(metricsInfo['mem'] / 1000).toFixed(2)} MB`

    const freeHeap = metricsInfo && `Free heap: ${(metricsInfo['heap.committed'] / 1000).toFixed(2)} MB`
    const usedHeap = metricsInfo &&
      `Used heap: ${((metricsInfo['heap'] - metricsInfo['heap.committed']) / 1000).toFixed(2)} MB`
    const totalHeap = metricsInfo && `Total heap: ${(metricsInfo['heap'] / 1000).toFixed(2)} MB`

    return (
      <div style={styles.main}>
        {userData &&
          <div style={styles.widgets}>
            <Panel title='Health'>
              <Table data={healthTableData} />
            </Panel>
            <Panel title='Disk'>
              <Table data={diskTableData} />
            </Panel>
            <Panel title='Memory'>
              <div style={styles.memChart}>
                <PieChart width={200} height={200}>
                  <Pie isAnimationActive={false} {...memChartProps} />
                </PieChart>
                <ul style={styles.info} >
                  <li><div style={[styles.tag, styles.tag.free]} />
                    {freeMemory}
                  </li>
                  <li><div style={[styles.tag, styles.tag.used]} />
                    {usedMemory}
                  </li>
                  <li><b>
                    {totalMemory}
                  </b></li>
                </ul>
              </div>
            </Panel>
            <Panel title='Heap'>
              <div style={styles.memChart}>
                <PieChart width={200} height={200}>
                  <Pie isAnimationActive={false} {...heapChartProps} />
                </PieChart>
                <ul style={styles.info} >
                  <li><div style={[styles.tag, styles.tag.free]} />
                    {freeHeap}
                  </li>
                  <li><div style={[styles.tag, styles.tag.used]} />
                    {usedHeap}
                  </li>
                  <li><b>
                    {totalHeap}
                  </b></li>
                </ul>
              </div>
            </Panel>
            <Panel style={{ flexGrow: 1 }} title='JVM Information'>
              <Table data={JVMTableData} />
            </Panel>
          </div>
        }
        <div style={styles.widgets}>
          <Panel style={{ flexGrow: 1 }} title='Application'>
            <Table data={appTableData} />
          </Panel>
        </div>
      </div>
    )
  }
}

Home.propTypes = {
  userData: PropTypes.object,
  dashboard: PropTypes.object.isRequired,
  getHealthInfo: PropTypes.func,
  getOpenHubInfo: PropTypes.func,
  getMetricsInfo: PropTypes.func

}

export default Home
