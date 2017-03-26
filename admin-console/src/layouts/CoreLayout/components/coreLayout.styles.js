import { sidebarWidth } from '../../../common/components/Sidebar/sidebar.styles'
import { transition } from '../../../styles/constants'

export default {
  main: {
    position: 'absolute',
    height: '100%',
    width: '100%'
  },
  body: {
    ...transition(),
    position: 'absolute',
    width: '100%',
    height: '100%',
    boxSizing: 'border-box',
    overflow: 'auto',
    extended: {
      paddingLeft: `${sidebarWidth}px`
    }
  }
}

