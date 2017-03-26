import { gap, itemSize } from '../../../styles/constants'
import { secondaryColor, lightColor } from '../../../styles/colors'

export default {
  main: {
    position: 'relative',
    boxSizing: 'border-box',
    minHeight: 200,
    marginTop: gap,
    marginLeft: gap,
    marginRight: gap,
    marginBottom: gap,
    width: '48%'
  },
  content: {
    position: 'relative'
  },
  title: {
    height: itemSize,
    lineHeight: `${itemSize}px`,
    backgroundColor: secondaryColor,
    color: lightColor,
    paddingLeft: gap
  }
}
