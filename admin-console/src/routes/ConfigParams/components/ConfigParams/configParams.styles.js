import { secondaryColor, lightColor, primaryColor } from '../../../../styles/colors'
import { gap, smallGap } from '../../../../styles/constants'

export default {
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
  },
  table: {
    position: 'relative',
    width: '100%',
    boxShadow: '0 2px 10px -2px silver',
    borderSpacing: 1
  },
  header: {
    fontSize: '0.7em',
    textAlign: 'left',
    backgroundColor: primaryColor,
    minWidth: 80
  }
}
