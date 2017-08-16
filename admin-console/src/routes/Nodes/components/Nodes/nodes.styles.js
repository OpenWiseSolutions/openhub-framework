import { gap } from '../../../../styles/constants'
import { positiveColor, negativeColor, warningColor } from '../../../../styles/colors'
import Styles from '../../../../styles/styles'

export default {
  ...Styles,
  main: {
    width: '100%'
  },
  state: {
    position: 'relative',
    width: '25px',
    height: '25px'
  },
  green:{
    backgroundColor: positiveColor
  },
  red:{
    backgroundColor: negativeColor
  },
  orange:{
    backgroundColor: warningColor
  },
  button: {
    marginRight: gap,
    backgroundColor: 'transparent'
  }
}
