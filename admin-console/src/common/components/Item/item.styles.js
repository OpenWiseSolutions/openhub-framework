import tc from 'tinycolor2'
import { darkColor } from '../../../styles/colors'
import { itemSize, gap } from '../../../styles/constants'

export default {
  main: {
    position: 'relative',
    minHeight: `${itemSize}px`,
    lineHeight: `${itemSize}px`,
    width: '100%',
    cursor: 'pointer',
    boxSizing: 'border-box',
    color: tc(darkColor).lighten(50),
    backgroundColor: 'transparent',
    whiteSpace: 'nowrap'
  },
  label: {
    paddingLeft: gap,
    paddingRight: gap
  },
  arrow: {
    float: 'right',
    paddingRight: gap
  },
  icon: {
    marginRight: gap
  },
  children: {
    position: 'relative',
    height: 'auto'
  }
}
