import { gap, itemSize } from '../../../styles/constants'
import { primaryColor, componentsColor } from '../../../styles/colors'

export default {
  main: {
    position: 'absolute',
    top: 0,
    left: 0,
    width: '100%',
    paddingLeft: gap,
    paddingRight: gap,
    lineHeight: `${itemSize}px`,
    backgroundColor: componentsColor,
    boxSizing: 'border-box'
  },
  title: {
    position: 'relative',
    color: primaryColor,
    fontWeight: 200
  },
  close: {
    position: 'absolute',
    right: 0,
    top: 0,
    color: primaryColor,
    paddingRight: gap,
    cursor: 'pointer'
  }
}
