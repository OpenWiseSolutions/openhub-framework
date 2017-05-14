import axios from 'axios'
import { toastr } from 'react-redux-toastr'

// ------------------------------------
// Constants
// ------------------------------------
const GET_NODES_SUCCESS = 'GET_NODES_SUCCESS'
const OPEN_NODE = 'OPEN_NODE_DETAIL'
const CLOSE_NODE = 'CLOSE_NODE_DETAIL'

// ------------------------------------
// Actions
// ------------------------------------

export const getNodes = () => (dispatch) => {
  axios.get('/web/admin/api/cluster/nodes')
    .then(({ data }) => {
      dispatch({ type: GET_NODES_SUCCESS, payload: data.data })
    })
    .catch(() => {
      toastr.error('Error fetching nodes!')
    })
}

export const openNode = (id) => (dispatch, getState) => {
  const { nodes: { allNodes } } = getState()
  const nodeDetail = allNodes.find(node => node.id === id)
  dispatch({
    type   : OPEN_NODE,
    payload: nodeDetail
  })
}

export const closeNode = () => ({
  type: CLOSE_NODE
})

export const updateNode = (id, payload) => (dispatch) => {
  const update = () => {
    axios.put(`/web/admin/api/cluster/nodes/${id}`)
      .then(() => {
        dispatch(closeNode())
        dispatch(getNodes())
        toastr.success('Node successfully updated')
      })
      .catch(() => {
        toastr.error('Node update failed')
      })
  }
  toastr.confirm('Are you sure that you want to update this node?', {
    onOk: () => update()
  })
}

export const deleteNode = (id) => (dispatch) => {
  const remove = () => {
    axios.delete(`/web/admin/api/cluster/nodes/${id}`)
      .then(() => {
        dispatch(closeNode())
        dispatch(getNodes())
        toastr.success('Node successfully deleted')
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
