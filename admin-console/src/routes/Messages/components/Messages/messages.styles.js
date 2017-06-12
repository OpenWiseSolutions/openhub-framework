import { gap, bigGap } from '../../../../styles/constants'
import styles from '../../../../styles/styles'

export default {
  ...styles,
  formContent: {
    display: 'flex',
    flexDirection: 'row'
  },
  column: {
    flex: 1,
    paddingLeft: gap,
    paddingRight: gap
  },
  datepicker: {
    boxSizing: 'border-box',
    paddingTop: 0,
    paddingBottom: 0
  },
  controls: {
    width: '100%',
    boxSizing: 'border-box',
    marginTop: gap,
    paddingRight: gap,
    paddingLeft: gap
  },
  control: {
    float: 'right',
    marginLeft: gap
  },
  messages: {
    top: bigGap
  },
  cell: {
    ...styles.cell,
    cursor: 'pointer'
  }
}
