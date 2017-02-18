import { primaryColor, secondaryColor } from '../../../styles/colors'
import { depth1, itemSize, gap, userMenuWidth } from '../../../styles/constants'
import { fadeIn } from 'react-animations'
import Radium from 'radium'

export default {
  main: {
    position: 'relative',
    height: `${itemSize}px`,
    lineHeight: `${itemSize}px`,
    cursor: 'pointer'
  },
  name: {
    fontSize: '0.9em',
    marginLeft: gap,
    marginRight: gap
  },
  arrow: {
    color: secondaryColor,
    marginRight: gap
  },
  menu: {
    animation: 'fadeIn 0.3s',
    animationName: Radium.keyframes(fadeIn, 'fadeIn'),
    position: 'absolute',
    top: `${itemSize}px`,
    right: 0,
    width: userMenuWidth,
    height: 'auto',
    backgroundColor: primaryColor,
    boxShadow: '0 0 5px 0 silver',
    zIndex: depth1
  }
}
