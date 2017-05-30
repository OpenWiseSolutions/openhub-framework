import { connect } from 'react-redux'
import { getWsdlOverview } from '../modules/wsdl.module'
import WSDL from '../components/WSDL/WSDL'

const mapDispatchToProps = {
  getWsdlOverview
}

const mapStateToProps = (state) => ({
  wsdlData: state.wsdl.wsdlData
})

export default connect(mapStateToProps, mapDispatchToProps)(WSDL)
