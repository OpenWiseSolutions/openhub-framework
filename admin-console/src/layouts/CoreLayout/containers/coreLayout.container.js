import * as actionCreators from '../actions/coreLayout.actions'
import * as authActions from '../../../common/actions/auth.actions'
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import CoreLayout from '../components/CoreLayout'

function mapStateToProps (state, props) {
  return {
    sidebarExtended: state.coreLayout.sidebarExtended,
    navbarUserExpanded: state.coreLayout.navbarUserExpanded,
    isAuth: state.auth.isAuth
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
