import tc from 'tinycolor2'
import { transition, itemSize, gap, bigGap, logo, depth1 } from '../../../styles/constants'
import { primaryColor, secondaryColor, lightColor, darkColor } from '../../../styles/colors'

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
    boxShadow: 'inset -10px 0px 15px -5px rgba(0,0,0,0.5)',
    zIndex: depth1,
    ...transition()
  },
  logo: {
    position: 'relative',
    width: '100%',
    height: itemSize,
    backgroundColor: primaryColor,
    backgroundImage: `url(${logo})`,
    backgroundSize: 'auto 80%',
    backgroundRepeat: 'no-repeat',
    backgroundPosition: '10% 50%',
    lineHeight: `${itemSize}px`,
    paddingLeft: gap,
    boxShadow: 'inset -10px 0px 15px -5px rgba(0,0,0,0.5)'
  },
  extended: {
    width: sidebarWidth
  },
  item: {
    backgroundColor: 'transparent',
    color: lightColor,
    fontWeight: 300,
    expanded: {
      boxShadow: `${tc(darkColor).setAlpha(0.4).toString()} 0 1px 0, 
      inset ${tc(lightColor).setAlpha(0.4).toString()} 0 1px 0`
    }
  },
  nestedItem: {
    paddingLeft: bigGap,
    color: lightColor,
    ':hover': {
      backgroundColor: tc(primaryColor).lighten(5).setAlpha(0.05).toString()
    }
  }
}
