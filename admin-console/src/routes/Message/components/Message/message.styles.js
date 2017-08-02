import styles from '../../../../styles/styles'
import { gap, bigGap } from '../../../../styles/constants'

export default {
  ...styles,
  main: {
    paddingTop: gap,
    paddingBottom: gap,
    paddingLeft: gap,
    paddingRight: gap
  },
  smallCell: {
    width: '30%'
  },
  code: {
    hljs: {
      background: 'transparent'
    }
  },
  controls: {
    display: 'flex',
    flexDirection: 'row',
    height: '60px',
    width: '300px',
    justifyContent: 'space-between',
    alignItems: 'center'
  },
  panel: {
    ...styles.panel,
    marginBottom: bigGap,
    minHeight: '100px'
  }
}
