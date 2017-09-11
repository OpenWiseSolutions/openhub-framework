/* eslint-disable max-len */

import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import FontIcon from 'react-md/lib/FontIcons'
import Button from 'react-md/lib/Buttons/Button'
import Card from 'react-md/lib/Cards/Card'
import DataTable from 'react-md/lib/DataTables/DataTable'
import TableHeader from 'react-md/lib/DataTables/TableHeader'
import TableBody from 'react-md/lib/DataTables/TableBody'
import TableRow from 'react-md/lib/DataTables/TableRow'
import TableColumn from 'react-md/lib/DataTables/TableColumn'
import SelectFieldColumn from 'react-md/lib/DataTables/SelectFieldColumn'
import styles from './nodes.styles'
import NodeModal from '../NodeModal/NodeModal'

const states = [
  { value: 'RUN', label: <div style={styles.icon}><FontIcon style={styles.positive}>play_arrow</FontIcon>Run</div> },
  { value: 'STOPPED', label: <div style={styles.icon}><FontIcon style={styles.negative} >stop</FontIcon>Stop</div> },
  { value: 'HANDLES_EXISTING_MESSAGES', label: <div style={styles.icon}><FontIcon style={styles.neutral}>inbox</FontIcon>Handle Existing Messages</div> }
]

@Radium
class ConfigParams extends Component {

  componentDidMount () {
    this.props.getNodes()
  }

  render () {
    const {
      nodes,
      nodeDetail,
      closeNode,
      openNode,
      updateNode,
      deleteNode
    } = this.props

    if (!nodes) return null

    const computedStyles = [styles.main]

    return (
      <div style={computedStyles} >
        {nodeDetail && <NodeModal
          updateNode={updateNode}
          data={nodeDetail}
          close={closeNode}
          isOpen={!!nodeDetail}
        />}
        <Card >
          <DataTable plain style={{ overflow: 'hidden' }}>
            <TableHeader >
              <TableRow >
                <TableColumn >Id</TableColumn >
                <TableColumn >Code</TableColumn >
                <TableColumn >Name</TableColumn >
                <TableColumn >Description</TableColumn >
                <TableColumn >State</TableColumn >
                <TableColumn >Actions</TableColumn >
              </TableRow >
            </TableHeader >
            <TableBody >
              {nodes.map((data) => (
                <TableRow key={data.id} >
                  <TableColumn >{data.id}</TableColumn >
                  <TableColumn >{data.code}</TableColumn >
                  <TableColumn >{data.name}</TableColumn >
                  <TableColumn >{data.description}</TableColumn >
                  <SelectFieldColumn
                    onChange={(s) => updateNode(data.id, { state: s }, data)}
                    defaultValue={data.state}
                    menuItems={states}
                  />
                  <TableColumn >
                    <Button primary onClick={() => openNode(data.id)} >edit</Button >
                    { nodes.length > 1 && <Button onClick={() => deleteNode(data.id)} >delete</Button > }
                  </TableColumn >
                </TableRow >
              ))}
            </TableBody >
          </DataTable >
        </Card >
      </div >
    )
  }
}

ConfigParams.propTypes = {
  getNodes: PropTypes.func,
  openNode: PropTypes.func,
  closeNode: PropTypes.func,
  updateNode: PropTypes.func,
  deleteNode: PropTypes.func,
  nodes: PropTypes.array,
  nodeDetail: PropTypes.object
}

export default ConfigParams
