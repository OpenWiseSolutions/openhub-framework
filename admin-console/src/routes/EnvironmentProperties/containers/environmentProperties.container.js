import { connect } from 'react-redux'
import { getEnvironmentData, getConfigData } from '../modules/environmentProperties.module'
import EnvironmentProperties from '../components/EnvironmentProperties'

const mapDispatchToProps = {
  getEnvironmentData,
  getConfigData
}

const mapStateToProps = ({ environmentProperties }) => (
  environmentProperties
)

export default connect(mapStateToProps, mapDispatchToProps)(EnvironmentProperties)
