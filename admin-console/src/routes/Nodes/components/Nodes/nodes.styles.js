import { gap } from '../../../../styles/constants'
import { primaryColor, lightColor, positiveColor, negativeColor, warningColor } from '../../../../styles/colors'

export default {
  main: {
    width: '100%'
  },
  panel: {
    width: '98%'
  },
  table: {
    position: 'relative',
    width: '100%',
    boxShadow: '0 2px 10px -2px silver',
    borderSpacing: 1
  },
  header:{
    textAlign: 'left',
    paddingLeft: 10
  },
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
