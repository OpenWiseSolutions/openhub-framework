import { gap } from '../../../../styles/constants'
import styles from '../../../../styles/styles'

export default {
  ...styles,
  main: {
    paddingTop: gap,
    paddingLeft: gap,
    paddingRight: gap,
    paddingBottom: gap
  },
  searchBox: {
    width: 400,
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'center'
  },
  counts: {
    marginLeft: gap
  },
  loggers: {
    marginTop: gap
  },
  controls: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center'
  }
}
