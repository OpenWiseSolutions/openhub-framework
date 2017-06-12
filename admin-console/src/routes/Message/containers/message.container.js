import { connect } from 'react-redux'
import Message from '../components/Message/Message'
import { actions } from '../modules/message.module'

const mapDispatchToProps = {
  ...actions
}

const mapStateToProps = ({ message }) => ({
  ...message
})

export default connect(mapStateToProps, mapDispatchToProps)(Message)
