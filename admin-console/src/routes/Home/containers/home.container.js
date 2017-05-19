import { connect } from 'react-redux'
import { actions } from '../modules/home.module'
import Home from '../components/Home'

const mapDispatchToProps = {
  ...actions
}

const mapStateToProps = (state) => ({
  dashboard: state.home,
  userData: state.auth.userData
})

export default connect(mapStateToProps, mapDispatchToProps)(Home)
