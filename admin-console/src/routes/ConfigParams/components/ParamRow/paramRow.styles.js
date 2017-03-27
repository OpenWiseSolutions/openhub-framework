import tc from 'tinycolor2'
import { primaryColor, lightColor, positiveColor } from '../../../../styles/colors'

export default {
  even: {
    backgroundColor: primaryColor
  },
  odd: {
    backgroundColor: lightColor
  },
  cell: {
    margin: 0,
    paddingTop: 10,
    paddingBottom: 10,
    paddingLeft: 10,
    paddingRight: 10
  },
  button: {
    backgroundColor: positiveColor,
    fontSize: '0.7em',
    paddingLeft: 10,
    paddingRight: 10,
    textAlign: 'center',
    cursor: 'pointer',
    ':hover': {
      backgroundColor: tc(positiveColor).lighten(10)
    }
  }
}
