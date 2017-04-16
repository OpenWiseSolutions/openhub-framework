import { gap, smallGap } from '../../../../styles/constants'
import { primaryColor } from '../../../../styles/colors'

export default {
  main: {
    position: 'relative',
    width: '100%',
    boxSizing: 'border-box',
    paddingLeft: gap,
    paddingRight: gap,
    paddingTop: gap,
    paddingBottom: gap,
    marginBottom: smallGap,
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: primaryColor
  },
  label: {
    flex: 1,
    fontWeight: 800
  },
  controls: {
    flex: 1,
    display: 'flex',
    justifyContent: 'center',
    fontSize: '0.9em',
    alignItems: 'center'
  },
  button: {
    paddingLeft: smallGap,
    paddingRight: smallGap,
    paddingTop: smallGap,
    paddingBottom: smallGap,
    boxShadow: '0 1px 1px 0 silver',
    cursor: 'pointer',
    backgroundColor: 'white',
    ':hover': {
      backgroundColor: 'lightgray'
    }
  },
  trace: {
    backgroundColor: 'yellow'
  },
  debug: {
    backgroundColor: 'green'
  },
  info: {
    backgroundColor: 'blue'
  },
  warn: {
    backgroundColor: 'orange'
  },
  error: {
    backgroundColor: 'red'
  },
  off: {
    backgroundColor: 'gray'
  }
}
