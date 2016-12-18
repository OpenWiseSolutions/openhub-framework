import { transition } from '../../styles/constants'
import { secondaryColor } from '../../styles/colors'

export const sidebarWidth = 200

export default {
  main: {
    position: 'absolute',
    top: 0,
    left: 0,
    width: 0,
    height: '100%',
    backgroundColor: secondaryColor,
    ...transition()
  },
  extended: {
    width: sidebarWidth
  }
}
