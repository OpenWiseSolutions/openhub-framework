import { actions as authActions } from '../../../common/modules/auth.module'
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import CoreLayout from '../components/CoreLayout'

const mapStateToProps = ({ auth }) => ({
  ...auth
})

const mapDispatchToProps = (dispatch) => ({
  authActions: bindActionCreators(authActions, dispatch)
})

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CoreLayout)
