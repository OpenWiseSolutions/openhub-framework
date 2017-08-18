import { connect } from 'react-redux'
import Messages from '../components/Messages/Messages'
import { actions } from '../modules/messages.module'
import { getCatalog } from '../../../common/modules/catalog.module'

const mapDispatchToProps = {
  ...actions,
  getCatalog
}

const mapStateToProps = ({ messages, catalogs }) => ({
  messageStates: catalogs['messageState'],
  ...messages
})

export default connect(mapStateToProps, mapDispatchToProps)(Messages)
