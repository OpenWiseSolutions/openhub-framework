import { primaryColor } from '../../../../styles/colors'
import styles from '../../../../styles/styles'
import { gap } from '../../../../styles/constants'

export default {
  ...styles,
  item: {
    paddingBottom: gap,
    marginTop: gap,
    boxShadow: '0 2px 7px -1px silver'
  },
  content: {
    paddingLeft: gap,
    paddingRight: gap
  },
  title: {
    paddingLeft: gap,
    paddingRight: gap,
    paddingTop: gap,
    paddingBottom: gap,
    backgroundColor: primaryColor
  }
}
