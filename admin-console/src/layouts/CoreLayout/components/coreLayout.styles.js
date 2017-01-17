import { sidebarWidth } from '../../../common/components/Sidebar/sidebar.styles'
import { dev, transition } from '../../../styles/constants'

export default {
  main: {
    position: 'absolute',
    height: '100%',
    width: '100%',
    ...dev
  },
  body: {
    ...transition(),
    ...dev,
    extended: {
      paddingLeft: `${sidebarWidth}px`
    }
  }
}

