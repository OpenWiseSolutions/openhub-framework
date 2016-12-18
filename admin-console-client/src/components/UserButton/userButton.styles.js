import { lightColor, secondaryColor } from '../../styles/colors'
import { itemSize, gap } from '../../styles/constants'

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
    position: 'absolute',
    top: `${itemSize}px`,
    right: 0,
    width: 300,
    minHeight: 200,
    backgroundColor: lightColor
  }
}
