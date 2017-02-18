import tc from 'tinycolor2'
import { itemSize, gap } from '../../../styles/constants'
import { primaryColor, secondaryColor } from '../../../styles/colors'

export default {
  main: {
    position: 'relative'
  },
  row: {
    position: 'relative',
    boxSizing: 'border-box',
    width: '100%',
    height: itemSize,
    lineHeight: `${itemSize}px`,
    paddingLeft: gap,
    paddingRight: gap
  },
  column: {
    position: 'relative',
    float: 'left',
    textOverflow: 'ellipsis',
    overflow: 'hidden',
    whiteSpace: 'nowrap'
  },
  even: {
    backgroundColor: tc(secondaryColor).setAlpha(0.1)
  },
  odd: {
    backgroundColor: tc(primaryColor).setAlpha(0.1)
  }
}
