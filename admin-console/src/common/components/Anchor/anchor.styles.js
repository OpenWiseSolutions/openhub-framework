import { secondaryColor } from '../../../styles/colors'
import { itemSize } from '../../../styles/constants'

export default {
  fontSize: '0.9em',
  lineHeight: `${itemSize}px`,
  cursor: 'pointer',
  color: secondaryColor,
  ':hover': {
    textDecoration: 'underline'
  }
}
