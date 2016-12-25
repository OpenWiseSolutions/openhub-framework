import tc from 'tinycolor2'
import { transition, itemSize, gap } from '../../styles/constants'
import { primaryColor, secondaryColor, lightColor, darkColor } from '../../styles/colors'

export const sidebarWidth = 200

export default {
  main: {
    position: 'absolute',
    top: 0,
    left: 0,
    width: 0,
    height: '100%',
    overflow: 'hidden',
    backgroundColor: secondaryColor,
    ...transition()
  },
  logo: {
    position: 'relative',
    width: '100%',
    height: itemSize,
    backgroundColor: primaryColor,
    lineHeight: `${itemSize}px`,
    paddingLeft: gap
  },
  extended: {
    width: sidebarWidth
  },
  item: {
    backgroundColor: 'transparent',
    color: lightColor,
    ':hover': {
      backgroundColor: tc(primaryColor).lighten(5).setAlpha(0.2).toString(),
      color: darkColor
    }
  },
  nestedItem: {
    color: lightColor,
    ':hover': {
      backgroundColor: 'transparent',
      color: darkColor
    }
  }
}
