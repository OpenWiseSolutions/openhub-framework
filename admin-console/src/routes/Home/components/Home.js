import React, { Component, PropTypes } from 'react'
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
    const { actions: { getHealthInfo } } = this.props
    getHealthInfo()
  }

  render () {
    const { isAuth, dashboard: { healthInfo, openHubInfo, metricsInfo } } = this.props
    const isUp = (val) => val === 'UP'

    const appTableData = openHubInfo && [
      ['Name', openHubInfo.name],
      ['Version', openHubInfo.version]
    ]

    const healthTableData = healthInfo && [
      ['Application', <Status status={isUp(healthInfo.status)} />],
      ['Datasources', <Status status={isUp(healthInfo.db.status)} />],
      ['Database', healthInfo.db.database]
    ]

    const diskTableData = healthInfo && [
      ['Status', <Status status={isUp(healthInfo.diskSpace.status)} />],
      ['Free space', healthInfo.diskSpace.free],
      ['Total space', healthInfo.diskSpace.total],
      ['Threshold', healthInfo.diskSpace.threshold]
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

    return (
      <div style={styles.main}>
        {isAuth &&
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
                  <Pie {...memChartProps} />
                </PieChart>
                <ul style={styles.info} >
                  <li><div style={[styles.tag, styles.tag.free]} />
                    Free memory: { metricsInfo['mem.free'] / 1000 }MB
                  </li>
                  <li><div style={[styles.tag, styles.tag.used]} />
                    Used memory: { metricsInfo['mem'] - metricsInfo['mem.free'] / 1000 }MB
                  </li>
                  <li><b>
                    Total memory: { metricsInfo['mem'] / 1000 }MB
                  </b></li>
                </ul>
              </div>
            </Panel>
            <Panel title='Heap'>
              <div style={styles.memChart}>
                <PieChart width={200} height={200}>
                  <Pie {...heapChartProps} />
                </PieChart>
                <ul style={styles.info} >
                  <li><div style={[styles.tag, styles.tag.free]} />
                    Free heap: { metricsInfo['heap.committed'] / 1000 }MB
                  </li>
                  <li><div style={[styles.tag, styles.tag.used]} />
                    Used heap: { metricsInfo['heap'] - metricsInfo['heap.committed'] / 1000 }MB
                  </li>
                  <li><b>
                    Total heap: { metricsInfo['heap'] / 1000 }MB
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
  isAuth: PropTypes.bool,
  actions: PropTypes.object.isRequired,
  dashboard: PropTypes.object.isRequired
}

export default Home
