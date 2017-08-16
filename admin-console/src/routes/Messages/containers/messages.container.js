import { connect } from 'react-redux'
import Messages from '../components/Messages/Messages'
import { actions } from '../modules/messages.module'

const mapDispatchToProps = {
  ...actions
}

const mapStateToProps = ({ messages }) => ({
  ...messages
})

export default connect(mapStateToProps, mapDispatchToProps)(Messages)
