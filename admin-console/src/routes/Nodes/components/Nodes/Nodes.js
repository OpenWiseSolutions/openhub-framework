import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import MdPlayArrow from 'react-icons/lib/md/play-arrow'
import MdStop from 'react-icons/lib/md/stop'
import MdInbox from 'react-icons/lib/md/inbox'
import Button from 'react-md/lib/Buttons/Button'
import Card from 'react-md/lib/Cards/Card'
import DataTable from 'react-md/lib/DataTables/DataTable'
import TableHeader from 'react-md/lib/DataTables/TableHeader'
import TableBody from 'react-md/lib/DataTables/TableBody'
import TableRow from 'react-md/lib/DataTables/TableRow'
import TableColumn from 'react-md/lib/DataTables/TableColumn'
import styles from './nodes.styles'
import { positiveColor, negativeColor, warningColor } from '../../../../styles/colors'
import NodeModal from '../NodeModal/NodeModal'

@Radium
class ConfigParams extends Component {

  componentDidMount () {
    this.props.getNodes()
  }

  getStateIcon (state) {
    switch (state) {
      case 'RUN':
        return <div title='The Node is Running' >
          <MdPlayArrow size={30} color={positiveColor} />
        </div >
      case 'STOPPED':
        return <div title='The Node is Stopped' >
          <MdStop size={30} color={negativeColor} />
        </div >
      case 'HANDLES_EXISTING_MESSAGES':
        return <div title='The Node handles only existing messages. New messages/requests are rejected' >
          <MdInbox size={25} color={warningColor} />
        </div >
    }
  }

  render () {
    const { nodes, nodeDetail, closeNode, openNode, updateNode, deleteNode } = this.props

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
          <DataTable plain >
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
              {nodes.map(({ id, code, name, description, state }) => (
                <TableRow key={id} >
                  <TableColumn style={styles.value} >{id}</TableColumn >
                  <TableColumn style={styles.value} >{code}</TableColumn >
                  <TableColumn style={styles.value} >{name}</TableColumn >
                  <TableColumn style={styles.value} >{description}</TableColumn >
                  <TableColumn style={styles.value} >{this.getStateIcon(state)}</TableColumn >
                  <TableColumn >
                    <Button primary onClick={() => openNode(id)} >edit</Button >
                    { nodes.length > 1 && <Button onClick={() => deleteNode(id)} >delete</Button > }
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
