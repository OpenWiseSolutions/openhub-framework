import { actions } from '../modules/auth.module'
import { connect } from 'react-redux'
import { LoginCard } from '../components'

const mapStateToProps = (state) => ({

})

const mapDispatchToProps = {
  ...actions
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(LoginCard)
