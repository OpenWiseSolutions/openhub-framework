import * as actionCreators from '../actions/coreLayout.actions'
import { actions as authActions } from '../../../common/modules/auth.module'
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import CoreLayout from '../components/CoreLayout'

function mapStateToProps (state, props) {
  return {
    sidebarExtended: state.coreLayout.sidebarExtended,
    navbarUserExpanded: state.coreLayout.navbarUserExpanded,
    authUser: state.auth.authUser
  }
}

function mapDispatchToProps (dispatch) {
  return {
    actions: bindActionCreators(actionCreators, dispatch),
    authActions: bindActionCreators(authActions, dispatch)
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CoreLayout)
