import PropTypes from 'prop-types'
import { Field } from 'valid-react-form'
import styles from './field.styles.js'
import { withContext } from 'recompose'

export default withContext({
  styles: PropTypes.object },
  () => ({ styles })
)(Field)
