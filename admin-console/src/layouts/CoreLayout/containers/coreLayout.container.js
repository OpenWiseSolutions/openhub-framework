import { connect } from 'react-redux'
import CoreLayout from '../components/CoreLayout'
import { actions } from '../coreLayout.module'

const mapStateToProps = ({ api, auth, layout }) => ({
  ...api,
  ...auth,
  ...layout
})

const mapDispatchToProps = {
  ...actions
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CoreLayout)
