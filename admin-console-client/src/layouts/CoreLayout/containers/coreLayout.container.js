import * as actionCreators from '../actions/coreLayout.actions'
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import CoreLayout from '../components/CoreLayout'

function mapStateToProps (state, props) {
  return {
    sidebarExtended: state.coreLayout.sidebarExtended
  }
}

function mapDispatchToProps (dispatch) {
  return {
    actions: bindActionCreators(actionCreators, dispatch)
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CoreLayout)
