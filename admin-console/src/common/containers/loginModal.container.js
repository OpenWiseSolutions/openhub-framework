import * as actionCreators from '../actions/auth.actions'
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import LoginModal from '../components/LoginModal/LoginModal'

function mapStateToProps (state) {
  return {
    loginModalOpen: state.auth.loginModalOpen,
    loginErrors: state.auth.loginErrors
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
)(LoginModal)
