import { connect } from 'react-redux'
import ToolbarMenu from '../components/ToolbarMenu'
import { actions } from '../../../common/modules/auth.module'

const mapDispatchToProps = {
  ...actions
}

const mapStateToProps = ({ auth }) => ({
  ...auth
})

export default connect(mapStateToProps, mapDispatchToProps)(ToolbarMenu)
