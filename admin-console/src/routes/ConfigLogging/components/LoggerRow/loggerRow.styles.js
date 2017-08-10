import tc from 'tinycolor2'
import { gap, smallGap } from '../../../../styles/constants'
import { secondaryColor, lightColor } from '../../../../styles/colors'
import styles from '../../../../styles/styles'

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
    ...styles.cell
  },
  label: {
    lineHeight: '40px'
  },
  controls: {
    flex: 1,
    display: 'flex',
    justifyContent: 'flex-start',
    fontSize: '0.9em',
    alignItems: 'center'
  },
  button: {
    paddingLeft: gap,
    paddingRight: gap,
    paddingTop: gap,
    paddingBottom: gap,
    cursor: 'pointer',
    marginRight: '1px',
    backgroundColor: tc(lightColor).setAlpha(0.5),
    ':hover': {
      backgroundColor: tc(secondaryColor).setAlpha(0.2)
    }
  },
  trace: {
    backgroundColor: tc('yellow').setAlpha(0.5),
    ':hover': {
      backgroundColor: tc('yellow').setAlpha(0.5)
    }
  },
  debug: {
    backgroundColor: tc('green').setAlpha(0.5),
    ':hover': {
      backgroundColor: tc('green').setAlpha(0.5)
    }
  },
  info: {
    backgroundColor: tc('blue').setAlpha(0.5),
    ':hover': {
      backgroundColor: tc('blue').setAlpha(0.5)
    }
  },
  warn: {
    backgroundColor: tc('orange').setAlpha(0.5),
    ':hover': {
      backgroundColor: tc('orange').setAlpha(0.5)
    }
  },
  error: {
    backgroundColor: tc('red').setAlpha(0.5),
    ':hover': {
      backgroundColor: tc('red').setAlpha(0.5)
    }
  },
  off: {
    backgroundColor: tc('gray').setAlpha(0.5),
    ':hover': {
      backgroundColor: tc('gray').setAlpha(0.5)
    }
  }
}
