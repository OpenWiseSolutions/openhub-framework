import Changes from '../components/Changes'
import { getChanges } from '../modules/changes.module'
import { connect } from 'react-redux'

const mapDispatchToProps = {
  getChanges
}

const mapStateToProps = (state) => ({
  changesData: state.changes.changesData
})

export default connect(mapStateToProps, mapDispatchToProps)(Changes)
