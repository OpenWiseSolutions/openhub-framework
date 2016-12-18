import { secondaryColor } from '../../styles/colors'
import { gap, itemSize } from '../../styles/constants'

export const avatarSize = itemSize * 0.8

export default {
  main: {
    height: `${avatarSize}px`,
    width: `${avatarSize}px`,
    borderRadius: '50%',
    overflow: 'hidden',
    display: 'block',
    float: 'left',
    marginRight: gap,
    marginTop: `${itemSize * 0.1}px`,
    backgroundColor: secondaryColor
  }
}

