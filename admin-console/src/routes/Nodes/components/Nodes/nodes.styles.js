import { positiveColor, negativeColor, warningColor } from '../../../../styles/colors'
import Styles from '../../../../styles/styles'

export default {
  ...Styles,
  main: {
    width: '100%'
  },
  icon:{
    display: 'flex',
    justifyContent: 'flex-start',
    alignItems: 'center'
  },
  positive: {
    color: positiveColor
  },
  negative: {
    color: negativeColor
  },
  neutral: {
    color: warningColor
  }
}
