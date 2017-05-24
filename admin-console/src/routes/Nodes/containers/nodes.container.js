import { connect } from 'react-redux'
import { getNodes, deleteNode, closeNode, openNode, updateNode } from '../modules/nodes.module'
import Nodes from '../components/Nodes/Nodes'

const mapDispatchToProps = {
  getNodes,
  openNode,
  closeNode,
  updateNode,
  deleteNode
}

const mapStateToProps = (state) => ({
  nodes: state.nodes.allNodes,
  nodeDetail: state.nodes.nodeDetail
})

export default connect(mapStateToProps, mapDispatchToProps)(Nodes)
