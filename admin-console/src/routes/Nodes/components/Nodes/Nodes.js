import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Radium from 'radium'
import styles from './nodes.styles'
import Panel from '../../../../common/components/Panel/Panel'
import Button from '../../../../common/components/Button/Button'
import NodeModal from '../NodeModal/NodeModal'

@Radium
class ConfigParams extends Component {

  componentDidMount () {
    this.props.getNodes()
  }

  getIcon (state) {
    switch (state) {
      case 'RUN':
        return <div title='The Node is Running' style={[styles.state, styles.green]} />
      case 'STOPPED':
        return <div title='The Node is Stopped' style={[styles.state, styles.red]} />
      case 'HANDLES_EXISTING_MESSAGES':
        return <div
          title='The Node handles only existing messages. New messages/requests are rejected'
          style={[styles.state, styles.orange]}
        />
    }
  }

  render () {
    const { nodes, nodeDetail, closeNode, openNode, updateNode, deleteNode } = this.props

    if (!nodes) return <div>Loading...</div>

    const computedStyles = [styles.main]

    return (
      <div style={computedStyles}>
        {nodeDetail && <NodeModal
          updateNode={updateNode}
          data={nodeDetail}
          close={closeNode}
          isOpen={!!nodeDetail}
        />}
        <Panel key={'ClusterNodes'} style={styles.panel} title={'Cluster Nodes'}>
          <table style={styles.table}>
            <tbody>
              <tr>
                <th style={styles.header}>Id</th>
                <th style={styles.header}>Code</th>
                <th style={styles.header}>Name</th>
                <th style={styles.header}>Description</th>
                <th style={styles.header}>State</th>
                <th style={styles.header}>Actions</th>
              </tr>
              { nodes.map(({ id, code, name, description, state }, index) => (
                <tr key={id} style={index % 2 === 0 ? styles.even : styles.odd}>
                  <td style={styles.cell}>{id}</td>
                  <td style={styles.cell}>{code}</td>
                  <td style={styles.cell}>{name}</td>
                  <td style={styles.cell}>{description}</td>
                  <td style={styles.cell}>{this.getIcon(state)}</td>
                  <td style={styles.cell}>
                    <Button onClick={() => openNode(id)} style={styles.button}>Update</Button>
                    <Button onClick={() => deleteNode(id)} style={styles.button}>Delete</Button>
                  </td>
                </tr>
            ))}
            </tbody>
          </table>
        </Panel>

      </div>
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
