import { primaryColor } from '../../../styles/colors'
import { itemSize } from '../../../styles/constants'

export default {
  main:{
    position: 'absolute',
    height: itemSize,
    lineHeight: `${itemSize}px`,
    width: '100%',
    left: 0,
    bottom: 0,
    backgroundColor: primaryColor,
    textAlign: 'center',
    fontWeight: 300,
    fontSize: '0.8em'
  }
}
