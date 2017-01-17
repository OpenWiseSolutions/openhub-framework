import tc from 'tinycolor2'
import { primaryColor } from '../../../styles/colors'
import { itemSize, gap } from '../../../styles/constants'

export default {
  fontSize: '0.9em',
  height: `${itemSize}px`,
  lineHeight: `${itemSize}px`,
  cursor: 'pointer',
  paddingTop: 0,
  paddingBottom: 0,
  outline: 'none',
  border: 'none',
  paddingLeft: gap,
  paddingRight: gap,
  backgroundColor: primaryColor,
  ':hover': {
    backgroundColor: tc(primaryColor).darken(10).toString()
  }
}
