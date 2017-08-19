import { toastr } from 'react-redux-toastr'
import { fetchNodes, editNode, removeNode } from '../../../services/nodes.service.js'

// ------------------------------------
// Constants
// ------------------------------------
const GET_NODES_SUCCESS = 'GET_NODES_SUCCESS'
const OPEN_NODE = 'OPEN_NODE_DETAIL'
const CLOSE_NODE = 'CLOSE_NODE_DETAIL'

// ------------------------------------
// Actions
// ------------------------------------

export const getNodesSuccess = ({ data }) => {
  return ({ type: GET_NODES_SUCCESS, payload: data })
}

export const getNodes = () => (dispatch) => {
  return fetchNodes()
    .then((data) => {
      dispatch(getNodesSuccess(data))
    })
}

export const openNode = (id) => (dispatch, getState) => {
  const { nodes: { allNodes } } = getState()
  const nodeDetail = allNodes.find(node => node.id === id)
  dispatch({
    type: OPEN_NODE,
    payload: nodeDetail
  })
}

export const closeNode = () => ({
  type: CLOSE_NODE
})

export const updateNode = (id, payload, data) => (dispatch) => {
  const stateChanged = !!payload.state
  const mergedPayload = {
    ...data,
    ...payload
  }
  const update = () => {
    return editNode(id, mergedPayload)
      .then(() => {
        dispatch(closeNode())
        dispatch(getNodes())
        toastr.success('Success', 'Node successfully updated')
      })
      .catch(() => {
        toastr.error('Node update failed')
      })
  }

  if (stateChanged) {
    toastr.confirm('Are you sure that you want to update this node?', {
      onOk: () => update()
    })
  } else {
    update()
  }
}

export const deleteNode = (id) => (dispatch) => {
  const remove = () => {
    return removeNode(id)
      .then(() => {
        dispatch(closeNode())
        dispatch(getNodes())
        toastr.success('Success', 'Node successfully deleted')
      })
      .catch(() => {
        toastr.error('Node delete failed')
      })
  }
  toastr.confirm('Are you sure that you want to delete this node?', {
    onOk: () => remove()
  })
}

// ------------------------------------
// Action Handlers
// ------------------------------------
const ACTION_HANDLERS = {
  [GET_NODES_SUCCESS]: (state, { payload }) => ({ ...state, allNodes: payload }),
  [OPEN_NODE]: (state, { payload }) => ({ ...state, nodeDetail: payload }),
  [CLOSE_NODE]: (state, { payload }) => ({ ...state, nodeDetail: null })
}

// ------------------------------------
// Reducer
// ------------------------------------
export const initialState = {
  allNodes: null,
  nodeDetail: null
}

export default function (state = initialState, action) {
  const handler = ACTION_HANDLERS[action.type]
  return handler ? handler(state, action) : state
}
