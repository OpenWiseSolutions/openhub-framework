import { getLoggers, updateLogger } from '../modules/configLogging.module'
import ConfigParams from '../components/ConfigLogging/ConfigLogging'
import { connect } from 'react-redux'

const mapDispatchToProps = {
  getLoggers,
  updateLogger
}
const mapStateToProps = (state) => ({
  loggingData: state.configLogging.loggingData
})

export default connect(mapStateToProps, mapDispatchToProps)(ConfigParams)
