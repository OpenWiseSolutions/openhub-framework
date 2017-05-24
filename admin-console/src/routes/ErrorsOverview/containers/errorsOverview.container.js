import { getErrorsOverview } from '../modules/errorsOverview.module'
import ErrorsOverview from '../components/ErrorsOverview/ErrorsOverview'
import { connect } from 'react-redux'

const mapDispatchToProps = {
  getErrorsOverview
}

const mapStateToProps = (state) => ({
  errorsData: state.errorsOverview.errorsData
})

export default connect(mapStateToProps, mapDispatchToProps)(ErrorsOverview)
