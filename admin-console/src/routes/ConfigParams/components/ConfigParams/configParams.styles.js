import styles from '../../../../styles/styles'
import { secondaryColor, lightColor } from '../../../../styles/colors'
import { gap, smallGap } from '../../../../styles/constants'

export default {
  ...styles,
  main: {
    boxSizing: 'border-box',
    paddingTop: gap,
    paddingBottom: smallGap,
    paddingLeft: smallGap,
    paddingRight: smallGap
  },
  category: {
    marginTop: 0,
    marginLeft: 0,
    marginRight: 0,
    marginBottom: gap,
    paddingTop: 0,
    paddingBottom: gap,
    paddingLeft: gap,
    paddingRight: gap
  },
  categoryTitle: {
    backgroundColor: secondaryColor,
    marginTop: 0,
    marginBottom: 0,
    marginLeft: 0,
    marginRight: 0,
    paddingRight: 0,
    paddingTop: gap,
    paddingBottom: gap,
    paddingLeft: gap,
    color: lightColor
  }
}
