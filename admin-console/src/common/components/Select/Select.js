import PropTypes from 'prop-types'
import { Select } from 'valid-react-form'
import styles from './select.styles.js'
import { withContext } from 'recompose'

export default withContext({
  styles: PropTypes.object },
  () => ({ styles })
)(Select)
