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
    paddingBottom: 0,
    marginTop: 0,
    marginBottom: 0,
    marginLeft: 0,
    marginRight: 0,
    width: '100%',
    height: '40px',
    border: 'none',
    outline: 'none',
    fontSize: '13px',
    borderBottomStyle: 'solid',
    borderBottomWidth: '1px',
    borderBottomColor: 'lightgray'
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
  },
  row: {
    cursor: 'pointer'
  }
}
